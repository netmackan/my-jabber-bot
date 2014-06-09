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

import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * Based on: http://www.javaprogrammingforums.com/java-networking-tutorials/551-how-write-simple-xmpp-jabber-client-using-smack-api.html
 *
 * @author Markus Kilås
 */
public class Sample1JabberBot extends AbstractJabberBot implements MessageListener, JabberBot {

    public Sample1JabberBot(String serverHost, int serverPort, String serviceName) {
        super(serverHost, serverPort, serviceName);
    }

    public void sendMessage(String message, String to) throws XMPPException {
        Chat chat = getConnection().getChatManager().createChat(to, this);
        chat.sendMessage(message);
    }

    public void displayBuddyList() {
        Roster roster = getConnection().getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for(RosterEntry r : entries) {
            System.out.println(r.getUser());
        }
    }

    @Override
    public void processMessage(Chat chat, Message msg) {
        if (msg.getType() == Message.Type.chat) {
            System.out.println(chat.getParticipant() + " says: " + msg.getBody());
        }
    }

}
