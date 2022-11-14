package com.labs.UI.impl.jfx;

import com.labs.UIAPI.command.identity.AuthorizeUserCommand;
import com.labs.core.service.IIdentityService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthPageController {
    @FXML private PasswordField Password;
    @FXML private TextField Name;
    @FXML private TextField SName;

    @FXML protected void handleSignInAction(ActionEvent event){
        IIdentityService i = MainFX.DI.get(IIdentityService.class);
        AuthorizeUserCommand au = new AuthorizeUserCommand(Name.getText(), SName.getText(), Password.getText());
        au.setServices(i);
        MainFX.Flow.Enqueue(au);
    }
}
