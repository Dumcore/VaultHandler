package com.epk.discord;

// import com.epk.discord.adapter.SheetConnector;
import com.epk.discord.dto.KnownItem;
import com.epk.discord.dto.VaultAccessDTO;
import com.epk.discord.hibernate.entity.VaultItem;
import com.epk.discord.hibernate.HibernateUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VaultHandler extends ListenerAdapter {

    private static JDA jda;
    private static Role adminRole;
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private Map<Long, List<String>> modalCache = new HashMap<>();
    private static final Logger log = JDALogger.getLog(VaultHandler.class);
    public static void main(String[] args) throws InterruptedException, IOException {

        log.info("Startup application and loading properties!");

        jda = JDABuilder.createLight(Configuration.get("vault_handler_token"), EnumSet.noneOf(GatewayIntent.class)) // slash commands don't need any intents
                .addEventListeners(new VaultHandler())
                .build();

        Thread.sleep(5000);

        Guild guild = jda.getGuildById(Configuration.get("guild_id"));

        CommandListUpdateAction commands = guild.updateCommands();

        commands.addCommands(
                Commands.slash("einlagern", "Nutzen beim Einlagern von Gegenständen in die Asservatenkammer.")
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand1", "Der Gegenstand, der gerade eingelagert wurde", true, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand2", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand3", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand4", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand5", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .setGuildOnly(true),
                Commands.slash("auslagern", "Nutzen beim Auslagern von Gegenständen in die Asservatenkammer.")
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand1", "Der Gegenstand, der gerade eingelagert wurde", true, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand2", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand3", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand4", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .addOptions(new OptionData(OptionType.STRING, "gegenstand5", "Der Gegenstand, der gerade eingelagert wurde", false, true))
                        .setGuildOnly(true),
                Commands.slash("history", "Zeigt eine Übersicht aller ein und ausgelagerten Dienstgegenständen eines Officers.")
                        .addOptions(new OptionData(OptionType.USER, "officer", "Officer, dessen Historie gezeigt werden soll", true, false))
                        .addOptions(new OptionData(OptionType.STRING, "startDatum", "Beginn des anzuzeigenden Zeitraums. (24.12.2024)", false, false))
                        .addOptions(new OptionData(OptionType.STRING, "endDatum", "Ende des anzuzeigenden Zeitraums (24.12.2024)", false, false))
                        .setGuildOnly(true)
        ).queue();

        adminRole = jda.getRoleById(Configuration.get("admin_role_id"));

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<VaultItem> allItems = session.createQuery("from VaultItem", VaultItem.class).getResultList();
            allItems.forEach(session::remove);
            log.error("items saved " + allItems.size());
            allItems.forEach(item -> log.info(Long.toString(item.getId())));
            transaction.commit();
            allItems = session.createQuery("from VaultItem", VaultItem.class).getResultList();
            allItems.forEach(item -> log.info(item.toString()));
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("einlagern") || event.getName().equals("auslagern")) {
            List<Command.Choice> options = Stream.of(KnownItem.values())
                    .filter(obj -> obj.label.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(obj -> new Command.Choice(obj.label, obj.label))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        log.debug("command /" + event.getName() + " executed by " + event.getMember().getIdLong() + " aka. " + event.getMember().getNickname());
        switch (event.getName())
        {
            case "auslagern":
                processVaultAccessCommand(event, false);
                break;
            case "einlagern":
                processVaultAccessCommand(event, true);
                break;
            case "history":
                if (event.getMember().getRoles().contains(adminRole)) {
                    event.reply("Du hast nicht die Berechtigung um diese Aktion auszuführen! Lasse dir dafür die Rolle " + adminRole.getAsMention() + " geben.").setEphemeral(true).queue();
                    return;
                }
                User officer = event.getOption("officer").getAsUser();
                String startDateString = event.getOption("startDate").getAsString();
                String endDateString = event.getOption("endDate").getAsString();
                // TODO: Continue here -> need 2 new queries and null check for dates. (maybe private method for validation)
                LocalDate startDate = LocalDate.parse(startDateString, dateFormat);
                LocalDate endDate = LocalDate.parse(endDateString, dateFormat);
                var accessLogs = HibernateUtil.getVaultAccessLogsByAccessorId(officer.getIdLong());
            default:
                log.info("Command '" + event.getName() + "' does not exist, but was issued by " + event.getMember().getNickname() + " with id: " + event.getMember().getIdLong());
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        switch (event.getModalId()) {
            case "auslagern":
                processVaultAccessModal(event, false);
                break;
            case "einlagern":
                processVaultAccessModal(event, true);
                break;
            default:
                log.error("Interaction for unregistered modal occurred and will be ignored. ModalId: " + event.getModalId());
        }
    }

    private MessageEmbed createVaultEmbed(VaultAccessDTO vaultAccessDTO, Interaction event) {

        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();
        /*
            Set the title:
            1. Arg: title as string
            2. Arg: URL as string or could also be null
         */
        eb.setTitle("Ein Officer hat die Asservatenkammer geöffnet!", null);

        /*
            Set the color
         */
        if (vaultAccessDTO.isPutIn()) {
            eb.setColor(Color.green);
        }
        else {
            eb.setColor(Color.red);
        }

        /*
            Set the text of the Embed:
            Arg: text as string
         */
        eb.setDescription("Officer <@"+event.getMember().getIdLong()+"> hat folgendes " + (vaultAccessDTO.isPutIn() ? "eingelagert" : "ausgelagert") + ":\n");

        /*
            Add fields to embed:
            1. Arg: title as string
            2. Arg: text as string
            3. Arg: inline mode true / false
         */
        if (vaultAccessDTO.getKnownItems().size() > 0) {
            eb.appendDescription("\n**Dienstausrüstung:**\n");
            for (var entry : vaultAccessDTO.getKnownItems().entrySet()) {
                eb.appendDescription(entry.getValue()+"x "+entry.getKey().label+"\n");
            }
        }

        if (vaultAccessDTO.getCustomItems().size() > 0) {
            eb.appendDescription("\n**Andere Gegenstände:**\n");
            for (var entry : vaultAccessDTO.getCustomItems().entrySet()) {
                eb.appendDescription(entry.getValue()+"x "+entry.getKey()+"\n");
            }
        }

        /*
            Add embed author:
            1. Arg: name as string
            2. Arg: url as string (can be null)
            3. Arg: icon url as string (can be null)
         */
        eb.setAuthor("LSSD | Asservatenkammer", null, event.getGuild().getIconUrl());//"https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");

        /*
            Set footer:
            1. Arg: text as string
            2. icon url as string (can be null)
         */
        eb.setFooter(event.getMember().getEffectiveName(), event.getMember().getAvatarUrl());

        eb.setTimestamp(event.getTimeCreated());

        /*
            Set image:
            Arg: image url as string
         */
        //eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");

        return eb.build();
    }

    private void processVaultAccessCommand(SlashCommandInteractionEvent event, boolean putIn) {
        log.info(String.valueOf(event.getGuild().getIdLong()));
        List<String> strings = event.getOptions().stream()
                .map(optionMapping -> optionMapping.getAsString())
                .distinct()
                .collect(Collectors.toList());

        modalCache.put(event.getMember().getIdLong(), strings);
        List<ActionRow> actionRows = new ArrayList<>();

        for (String string : strings) {
            TextInput field = TextInput.create(string+"Id", "Wie viel " + string + " ?", TextInputStyle.SHORT)
                    .setMaxLength(4)
                    .setRequired(true)
                    .build();
            actionRows.add(ActionRow.of(field));
        }
        Modal modal;
        if (putIn) {
            modal = Modal.create("einlagern", "Wie viel soll eingelagert werden?")
                    .addComponents(actionRows).build();
        }
        else {
            modal = Modal.create("auslagern", "Wie viel soll ausgelagert werden?")
                    .addComponents(actionRows).build();
        }


        event.replyModal(modal).queue(); // "Here are your selected objects: " + Arrays.toString(strings.toArray())
    }

    private void processVaultAccessModal(ModalInteractionEvent event, boolean putIn) {
        List<String> items = modalCache.get(event.getMember().getIdLong());
        modalCache.remove(event.getMember().getIdLong());

        List<Integer> amounts = event.getValues().stream()
                .map(input -> Integer.parseInt(input.getAsString()))
                .toList();

        VaultAccessDTO vaultAccessDTO = new VaultAccessDTO(putIn, event.getMember().getIdLong());

        int i = 0;
        for (String item : items) {
            vaultAccessDTO.addItem(item, amounts.get(i));
            i++;
        }

        // Persisting only items works, but persisting composite entity (VaultAccessLog) does not seem to persist items!
        //Set<VaultItem> vaultItem = vaultAccessDTO.toVaultAccessLog().getItems();
        //vaultItem.forEach(HibernateUtil::persistEntity);
        HibernateUtil.persistEntity(vaultAccessDTO.toVaultAccessLog());



        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<VaultItem> allItems = session.createQuery("from VaultItem", VaultItem.class).getResultList();
            log.error("items saved " + allItems.size());
            allItems.forEach(item -> log.info(Long.toString(item.getId())));
        } catch (Exception e) {
            log.error("Exception occurred one fetch of VaultItems ", e);
        }

        log.info((vaultAccessDTO.isPutIn() ? "In:" : "Out:") + " The officer " + event.getMember().getIdLong() + " aka. " + event.getMember().getEffectiveName() + " accessed the vault! " +
                "Persist VaultAccessLog");
        event.replyEmbeds(createVaultEmbed(vaultAccessDTO, event)).queue();
    }

}
