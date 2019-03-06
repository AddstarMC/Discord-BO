package au.com.addstar.discord.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.AllArgsConstructor;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/03/2019.
 */
@AllArgsConstructor
public class CommandContext {
    
    private MessageCreateEvent event;
    
    
}
