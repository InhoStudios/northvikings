package tech.inhostudios.vikingbot;

import javax.security.auth.login.LoginException;

public class Viking {

    public static void main(String[] args){

        try {
            BotFrame botFrame = new BotFrame(1280,720,"North Vikings");
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }

}
