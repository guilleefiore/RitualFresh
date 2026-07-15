import { useEffect, useMemo, useRef, useState } from 'react';
import { useAuth } from '../../auth/hooks/useAuth.js';
import {
  createConversation,
  listConversations,
  listMessages,
  markMessagesRead,
  openChatSocket,
  sendMessage,
  sendPresenceHeartbeat,
} from '../services/chatService.js';
import '../styles/chat.css';

const QUICK_MESSAGES = {
  CLIENT: ['Muchas gracias', 'Perfecto', 'Nos vemos pronto'],
  WORKER: ['Estoy en camino', 'Estoy afuera', 'Ya llego'],
};

const MAX_MESSAGE_LENGTH = 500;

export function ChatPage() {
  const { user } = useAuth();
  const [conversations, setConversations] = useState([]);
  const [activeConversationId, setActiveConversationId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState('');
  const [otherUserId, setOtherUserId] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [hasMoreMessages, setHasMoreMessages] = useState(true);
  const [busyAction, setBusyAction] = useState('');
  const activeConversationIdRef = useRef(null);
  const messageListRef = useRef(null);

  const activeConversation = conversations.find((conversation) => conversation.id === activeConversationId) || null;
  const quickMessages = QUICK_MESSAGES[user?.role] || [];
  const showCounter = draft.length >= MAX_MESSAGE_LENGTH - 40;
  const isReadOnly = activeConversation?.status === 'READ_ONLY';

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const data = await listConversations();
        if (!mounted) return;
        setConversations(data);
        setActiveConversationId((current) => current || data[0]?.id || null);
      } catch (error) {
        if (mounted) setErrorMessage(error.message);
      } finally {
        if (mounted) setIsLoading(false);
      }
    }

    load();
    return () => {
      mounted = false;
    };
  }, []);

  useEffect(() => {
    activeConversationIdRef.current = activeConversationId;
  }, [activeConversationId]);

  useEffect(() => {
    if (!activeConversationId) {
      setMessages([]);
      return;
    }

    let mounted = true;
    setErrorMessage('');
    setHasMoreMessages(true);

    async function loadActiveMessages() {
      try {
        const data = await listMessages(activeConversationId);
        if (!mounted) return;
        setMessages(data);
        setHasMoreMessages(data.length === 50);
        markVisibleMessagesRead(activeConversationId, data);
        requestAnimationFrame(scrollToBottom);
      } catch (error) {
        if (mounted) setErrorMessage(error.message);
      }
    }

    loadActiveMessages();
    return () => {
      mounted = false;
    };
  }, [activeConversationId]);

  useEffect(() => {
    const socket = openChatSocket((event) => {
      if (event.type === 'MESSAGE_CREATED') {
        handleRealtimeMessage(event.payload);
      }
      if (event.type === 'MESSAGE_READ') {
        handleRealtimeRead(event.payload);
      }
      if (event.type === 'CONVERSATION_UPDATED') {
        upsertConversation(event.payload);
      }
    });

    return () => socket.close();
  }, []);

  useEffect(() => {
    sendPresenceHeartbeat().catch(() => null);
    const timer = window.setInterval(() => {
      sendPresenceHeartbeat().catch(() => null);
    }, 25000);
    return () => window.clearInterval(timer);
  }, []);

  const unreadTotal = useMemo(
    () => conversations.reduce((total, conversation) => total + Number(conversation.unreadCount || 0), 0),
    [conversations]
  );

  async function handleCreateConversation(event) {
    event.preventDefault();
    if (!otherUserId) return;

    setBusyAction('create');
    setErrorMessage('');
    try {
      const conversation = await createConversation(otherUserId);
      upsertConversation(conversation);
      setActiveConversationId(conversation.id);
      setOtherUserId('');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleSend(content) {
    const normalized = content.trim();
    if (!activeConversation || !normalized || normalized.length > MAX_MESSAGE_LENGTH || isReadOnly) {
      return;
    }

    setBusyAction('send');
    setErrorMessage('');
    const clientMessageId = `local-${Date.now()}-${Math.random().toString(16).slice(2)}`;
    try {
      await sendMessage(activeConversation.id, normalized, clientMessageId);
      setDraft('');
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleLoadMore() {
    if (!activeConversationId || !messages[0] || isLoadingMore || !hasMoreMessages) return;

    setIsLoadingMore(true);
    try {
      const olderMessages = await listMessages(activeConversationId, messages[0].id);
      setMessages((current) => mergeMessages([...olderMessages, ...current]));
      setHasMoreMessages(olderMessages.length === 50);
      markVisibleMessagesRead(activeConversationId, olderMessages);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsLoadingMore(false);
    }
  }

  function handleRealtimeMessage(message) {
    upsertConversationUnread(message);
    if (message.conversationId !== activeConversationIdRef.current) {
      return;
    }

    setMessages((current) => mergeMessages([...current, message]));
    if (message.senderId !== user?.id) {
      markVisibleMessagesRead(message.conversationId, [message]);
    }
    requestAnimationFrame(scrollToBottom);
  }

  function handleRealtimeRead(payload) {
    const ids = new Set(payload.messageIds || []);
    setMessages((current) => current.map((message) => (
      ids.has(message.id) ? { ...message, readAt: payload.readAt } : message
    )));
  }

  function upsertConversation(conversation) {
    setConversations((current) => {
      const next = [conversation, ...current.filter((item) => item.id !== conversation.id)];
      return next.sort(compareConversations);
    });
  }

  function upsertConversationUnread(message) {
    setConversations((current) => current.map((conversation) => {
      if (conversation.id !== message.conversationId) return conversation;
      const isActive = conversation.id === activeConversationIdRef.current;
      const shouldIncrement = message.senderId !== user?.id && !isActive;
      return {
        ...conversation,
        lastMessageAt: message.createdAt,
        unreadCount: shouldIncrement ? Number(conversation.unreadCount || 0) + 1 : conversation.unreadCount,
      };
    }).sort(compareConversations));
  }

  async function markVisibleMessagesRead(conversationId, visibleMessages) {
    const unreadIncomingIds = visibleMessages
      .filter((message) => message.senderId !== user?.id && !message.readAt)
      .map((message) => message.id);
    if (!unreadIncomingIds.length) return;

    try {
      const response = await markMessagesRead(conversationId, unreadIncomingIds);
      handleRealtimeRead(response);
      setConversations((current) => current.map((conversation) => (
        conversation.id === conversationId
          ? { ...conversation, unreadCount: Math.max(0, Number(conversation.unreadCount || 0) - response.messageIds.length) }
          : conversation
      )));
    } catch {
      // La lectura puede reintentarse al recargar; no bloquea el uso del chat.
    }
  }

  function scrollToBottom() {
    const element = messageListRef.current;
    if (element) {
      element.scrollTop = element.scrollHeight;
    }
  }

  return (
    <main className="screen chat-screen">
      <section className="chat-shell">
        <aside className="chat-sidebar" aria-label="Conversaciones">
          <div className="chat-sidebar__header">
            <div>
              <p className="eyebrow">Chat</p>
              <h1>Mensajes</h1>
            </div>
            {unreadTotal > 0 ? <span className="chat-badge">{unreadTotal}</span> : null}
          </div>

          <form className="chat-create" onSubmit={handleCreateConversation}>
            <label htmlFor="otherUserId">Abrir por ID de usuario</label>
            <div>
              <input
                id="otherUserId"
                type="number"
                min="1"
                value={otherUserId}
                onChange={(event) => setOtherUserId(event.target.value)}
                placeholder="ID cliente/trabajador"
              />
              <button className="button button--primary" type="submit" disabled={busyAction === 'create'}>
                Abrir
              </button>
            </div>
          </form>

          {isLoading ? <p className="muted">Cargando conversaciones...</p> : null}
          {!isLoading && !conversations.length ? <p className="muted">Todavía no hay conversaciones habilitadas.</p> : null}
          <div className="conversation-list">
            {conversations.map((conversation) => (
              <button
                key={conversation.id}
                className={`conversation-item ${conversation.id === activeConversationId ? 'conversation-item--active' : ''}`}
                type="button"
                onClick={() => setActiveConversationId(conversation.id)}
              >
                <span>{conversation.otherParticipant.firstName} {conversation.otherParticipant.lastName}</span>
                <small>{formatPresence(conversation.presence)}</small>
                {conversation.unreadCount > 0 ? <strong>{conversation.unreadCount}</strong> : null}
              </button>
            ))}
          </div>
        </aside>

        <section className="chat-panel" aria-label="Sala de chat">
          {activeConversation ? (
            <>
              <header className="chat-panel__header">
                <div>
                  <p className="eyebrow">Conversación</p>
                  <h2>{activeConversation.otherParticipant.firstName} {activeConversation.otherParticipant.lastName}</h2>
                  <p className="muted">{formatPresence(activeConversation.presence)}</p>
                </div>
                {isReadOnly ? <span className="chat-status">Solo lectura</span> : null}
              </header>

              {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

              <div className="message-list" ref={messageListRef}>
                {hasMoreMessages ? (
                  <button className="load-more" type="button" onClick={handleLoadMore} disabled={isLoadingMore}>
                    {isLoadingMore ? 'Cargando...' : 'Cargar mensajes anteriores'}
                  </button>
                ) : null}
                {messages.map((message) => (
                  <article key={message.id} className={`message ${message.senderId === user?.id ? 'message--own' : 'message--other'}`}>
                    <p>{message.content}</p>
                    <footer>
                      <time>{formatTime(message.createdAt)}</time>
                      {message.senderId === user?.id ? <span className={message.readAt ? 'ticks ticks--read' : 'ticks'}>✓✓</span> : null}
                    </footer>
                  </article>
                ))}
              </div>

              <div className="quick-messages" aria-label="Mensajes rápidos">
                {quickMessages.map((message) => (
                  <button key={message} type="button" onClick={() => handleSend(message)} disabled={isReadOnly || busyAction === 'send'}>
                    {message}
                  </button>
                ))}
              </div>

              <form className="message-composer" onSubmit={(event) => { event.preventDefault(); handleSend(draft); }}>
                <textarea
                  value={draft}
                  maxLength={MAX_MESSAGE_LENGTH}
                  onChange={(event) => setDraft(event.target.value)}
                  placeholder={isReadOnly ? 'La conversación está en solo lectura.' : 'Escribí un mensaje...'}
                  disabled={isReadOnly}
                />
                <div className="composer-actions">
                  {showCounter ? <span className={draft.length >= MAX_MESSAGE_LENGTH ? 'counter counter--limit' : 'counter'}>{draft.length}/500</span> : null}
                  <button className="button button--primary" type="submit" disabled={isReadOnly || busyAction === 'send' || !draft.trim()}>
                    Enviar
                  </button>
                </div>
              </form>
            </>
          ) : (
            <div className="chat-empty">
              <p className="eyebrow">M05</p>
              <h2>Seleccioná o abrí una conversación</h2>
              <p className="muted">Hasta que M04 esté integrado, podés abrir una conversación con el ID del otro usuario.</p>
              {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

function mergeMessages(messages) {
  const byId = new Map(messages.map((message) => [message.id, message]));
  return Array.from(byId.values()).sort((a, b) => a.id - b.id);
}

function compareConversations(a, b) {
  return new Date(b.lastMessageAt || 0).getTime() - new Date(a.lastMessageAt || 0).getTime();
}

function formatPresence(presence) {
  if (presence?.online) return 'En línea';
  if (!presence?.lastSeenAt) return 'Sin actividad reciente';
  const minutes = Math.max(1, Math.round((Date.now() - new Date(presence.lastSeenAt).getTime()) / 60000));
  return `Última vez hace ${minutes} min`;
}

function formatTime(value) {
  if (!value) return '';
  return new Intl.DateTimeFormat('es-AR', { hour: '2-digit', minute: '2-digit' }).format(new Date(value));
}
