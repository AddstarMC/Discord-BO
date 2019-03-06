package au.com.addstar.discord.commands;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * Created for the AddstarMC Project. Created by Narimm on 6/03/2019.
 */
@Getter
@AllArgsConstructor
public abstract class Command {
    
    private CommandPerm permission;
    private List<String> aliases;
    private String name;
    
    public abstract Mono<Void> execute(CommandContext context);
    
    public abstract Consumer<EmbedCreateSpec> getHelp(CommandContext context);
    
}
