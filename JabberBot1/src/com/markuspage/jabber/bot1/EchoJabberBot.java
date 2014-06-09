/*
 * Copyright (C) 2014 markus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.markuspage.jabber.bot1;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * Bot echoing back everything said to it.
 *
 * @author Markus Kilås
 */
public class EchoJabberBot extends AbstractJabberBot {

    private final MessageListener messageListener = new MessageListener() {

        @Override
        public void processMessage(Chat chat, Message msg) {
            if (msg.getType() == Message.Type.chat && msg.getBody() != null) {
                try {
                    chat.sendMessage(msg.getBody());
                } catch (XMPPException ex) {
                    Logger.getLogger(EchoJabberBot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    };

    public EchoJabberBot(String serverHost, int serverPort, String serviceName) {
        super(serverHost, serverPort, serviceName);
    }

    @Override
    public void login(String userName, String password) throws XMPPException {
        super.login(userName, password);
        getConnection().getChatManager().addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(messageListener);
                System.out.println("Chat started with: " + chat.getParticipant());
            }
        });
    }

}
