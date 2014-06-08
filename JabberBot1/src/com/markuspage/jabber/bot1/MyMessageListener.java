/*
 * Copyright (C) 2014 Markus Kilås
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

import java.net.InetAddress;
import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * Based on: http://www.javaprogrammingforums.com/java-networking-tutorials/551-how-write-simple-xmpp-jabber-client-using-smack-api.html
 *
 * @author Markus Kilås
 */
public class MyMessageListener implements MessageListener {

    private XMPPConnection connection;
    private final String serverHost;
    private final int serverPort;

    public MyMessageListener(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void login(String userName, String password) throws XMPPException {
        ConnectionConfiguration config = new ConnectionConfiguration(serverHost, serverPort);
        connection = new XMPPConnection(config);

        connection.connect();
        connection.login(userName, password);
    }

    public void sendMessage(String message, String to) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(to, this);
        chat.sendMessage(message);
    }

    public void displayBuddyList() {
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for(RosterEntry r : entries) {
            System.out.println(r.getUser());
        }
    }

    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public void processMessage(Chat chat, Message msg) {
        if (msg.getType() == Message.Type.chat) {
            System.out.println(chat.getParticipant() + " says: " + msg.getBody());
        }
    }

}
