package com.ritualfresh.chat.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.chat.model.Conversation;
import com.ritualfresh.chat.model.ConversationStatus;
import com.ritualfresh.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

@Service
public class DefaultChatAccessPolicy implements ChatAccessPolicy {
    @Override
    public void validateCanCreateOrReactivate(User currentUser, User otherUser) {
        if (currentUser.getId().equals(otherUser.getId())) {
            throw new BusinessRuleException("No puede crear una conversacion consigo mismo.");
        }

        boolean validPair = currentUser.getRole() == UserRole.CLIENT && otherUser.getRole() == UserRole.WORKER
                || currentUser.getRole() == UserRole.WORKER && otherUser.getRole() == UserRole.CLIENT;
        if (!validPair) {
            throw new BusinessRuleException("El chat solo esta disponible entre cliente y trabajador.");
        }

        if (!otherUser.isActive()) {
            throw new BusinessRuleException("El usuario indicado no se encuentra activo.");
        }
    }

    @Override
    public void validateCanSendMessage(Conversation conversation, User sender) {
        if (!conversation.hasParticipant(sender.getId())) {
            throw new BusinessRuleException("No pertenece a esta conversacion.");
        }

        if (conversation.getStatus() == ConversationStatus.READ_ONLY) {
            throw new BusinessRuleException("La conversacion se encuentra en modo solo lectura.");
        }
    }
}
