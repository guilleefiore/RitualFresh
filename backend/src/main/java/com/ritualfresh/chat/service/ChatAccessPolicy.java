package com.ritualfresh.chat.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.chat.model.Conversation;

public interface ChatAccessPolicy {
    void validateCanCreateOrReactivate(User currentUser, User otherUser);

    void validateCanSendMessage(Conversation conversation, User sender);
}
