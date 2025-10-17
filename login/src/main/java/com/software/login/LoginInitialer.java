package com.software.login;

import com.software.router.IRouter;
import com.software.router.Router;

public class LoginInitialer implements IRouter {

    @Override
    public void register() {
        Router.register("/login", LoginActivity.class);
    }
}
