package com.kynn.reevo_backend.user.event;

import com.kynn.reevo_backend.user.internal.domain.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final Account account;
    private final String displayName;

    public UserRegisteredEvent(Object source, Account account, String displayName) {
        super(source);
        this.account = account;
        this.displayName = displayName;
    }
}