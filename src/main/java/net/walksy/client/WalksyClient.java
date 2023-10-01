package net.walksy.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.walksy.client.commands.*;
import net.walksy.client.commands.exploits.DiscardLocal;
import net.walksy.client.commands.exploits.ParticleCrash;
import net.walksy.client.commands.nbt.GiveCommand;
import net.walksy.client.commands.nbt.ItemEggCommand;
import net.walksy.client.commands.nbt.NbtCommand;
import net.walksy.client.commands.structures.CommandHandler;
import net.walksy.client.commands.structures.ModuleCommand;
import net.walksy.client.components.systems.FriendsManager;
import net.walksy.client.components.systems.Rotator;
import net.walksy.client.config.GeneralConfig;
import net.walksy.client.config.Persistence;
import net.walksy.client.events.EventEmitter;
import net.walksy.client.modules.Module;
import net.walksy.client.modules.chat.CommandAutoFill;
import net.walksy.client.modules.chat.InfChat;
import net.walksy.client.modules.combat.*;
import net.walksy.client.modules.hud.*;
import net.walksy.client.modules.packet.*;
import net.walksy.client.modules.render.*;
import net.walksy.client.modules.utilities.*;
import net.walksy.client.modules.walksymodules.*;
import net.walksy.client.utils.*;
import net.walksy.client.walksyevent.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map.Entry;

public class WalksyClient {
    private static WalksyClient instance = null;
    private EventManager eventManager;
    private Rotator rotator;

    public static final HashMap<Entity, Integer> toKill = new HashMap<>();

    private CrystalDataTrackerUtils crystalDataTracker;



    /**
     * Get's the singleton instance of Walksy Client
     */
    public static WalksyClient getInstance() {
        if (instance == null) {
            instance = new WalksyClient();
        }

        return instance;
    }



    // Variables
    private final static String CHAT_PREFIX = ChatUtils.chatPrefix("Walksy Client");

    private static final Logger LOGGER = LogManager.getLogger("Walksy Client");

    public CommandHandler commandHandler;
    public EventEmitter emitter = new EventEmitter();
    public FriendsManager friendsManager = new FriendsManager();
    public TextRenderer textRenderer;
    public GeneralConfig config;

    /**
     * Log an object's value to the console
     * @param obj The object to log
     */
    public static void log(Object obj) {
        log(obj.toString());
    }

    /**
     * Log a string to the console
     * @param str The string to log
     */
    public static void log(String str) {
        // I don't want no rats
        str = str.replaceAll("jndi:ldap", "sug:ma");

        LOGGER.info(str);
    }

    /**
     * Update Walksy Client's default font to a given Minecraft font ID
     * @param fontId The font's identifier
     */
    public void updateFont(Identifier fontId) {
        this.textRenderer = FontUtils.createTextRenderer(fontId);
    }

    /**
     * Update Walksy Client's default font to a given minecraft font ID
     * @param id The Minecraft font ID
     */
    public void updateFont(String id) {
        this.updateFont(new Identifier(id));
    }

    // Commands

    /**
     * Register commands
     */
    private void registerCommands() {
        // Add the font command
        this.commandHandler.registerCommand(new FontCommand());

        // Add the friends command
        this.commandHandler.registerCommand(new FriendsCommand(this.friendsManager));

        // Add Panic command
        this.commandHandler.registerCommand(new PanicCommand());

        // Add copyip command
        this.commandHandler.registerCommand(new CopyServerIPCommand());

        // Add nbt commands
        this.commandHandler.registerCommand(new NbtCommand());
        this.commandHandler.registerCommand(new GiveCommand());
        this.commandHandler.registerCommand(new ItemEggCommand());

        // Exploit commands
        this.commandHandler.registerCommand(new ParticleCrash());
        this.commandHandler.registerCommand(new DiscardLocal());

        // TODO replace this with just a Module sub command
        // Waypoints commands
        //commandHandler.registerCommand(new WaypointsCommand(
           // ((Waypoints)modules.get("waypoints")).waypoints
       // ));
    }

    /**
     * Register all of the module's commands
     */
    private void registerModuleCommands() {
        // Add all of the modules as commands.
        for (Entry<String, Module> entry : modules.entrySet()) {
            commandHandler.registerCommand(new ModuleCommand(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Module storage
     */
    private HashMap<String, Module> modules = new HashMap<String, Module>();

    /**
     * Gets the modules storage
     * @return The modules storage
     */
    public HashMap<String, Module> getModules() {
        return modules;
    }

    // Chat
    public void processChatPost(String message, CallbackInfo ci) {
        // Command Handling
        Integer commandHandlerOutput = commandHandler.handle(message);

        switch (commandHandlerOutput) {
            case -1:
                break;
            
            case 0: {
                // TODO have it display the command's help text.
                displayChatMessage(String.format("%sUnknown Command: Use 'help' for a list of commands.", ChatUtils.RED));
            }

            default: {
                ci.cancel();
            }
        }
    }
    public static void displayChatMessage(String message) {
        ChatUtils.displayMessage(CHAT_PREFIX + message);
    }

    // Client

    /**
     * Get's the Minecraft Client instance
     * @return The Minecraft Client instance
     */
    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    /**
     * Get's the Client Player Entity
     * @return The Client Player Entity
     */
    public static ClientPlayerEntity me() {
        // Get the client
        MinecraftClient client = getClient();

        // Return localplayer
        return client.player;
    }

    /**
     * Get the time in milliseconds
     * @return The time in milliseconds
     */
    public static double getCurrentTime() {
        return Double.valueOf(System.currentTimeMillis()) / 1000d; // Seconds
    }

    public void close() {
        log("Saving Client Config...");
        Persistence.saveConfig();

    }

    /**
     * Registers all of the given modules
     */
    private void registerModules() {
        // Load up all the modules

        this.registerModule(new EntityESP());
        this.registerModule(new NoWeather());
        this.registerModule(new NoBoss());
        this.registerModule(new XRay());
        this.registerModule(new TapeMeasure());
        this.registerModule(new ModList());
        this.registerModule(new NoHurtCam());
        this.registerModule(new FullBright());
        this.registerModule(new AutoRespawn());
        this.registerModule(new NoFireCam());
        this.registerModule(new Waypoints());
        this.registerModule(new ItemRenderTweaks());
        this.registerModule(new Tracers());
        this.registerModule(new BlockESP());
        this.registerModule(new ShulkerPeak());
        this.registerModule(new NoSubmerge());
        this.registerModule(new Watermark());
        this.registerModule(new FreeCam());
        this.registerModule(new AntiInvisible());
        this.registerModule(new NoRespondAlert());
        this.registerModule(new Binds());
        this.registerModule(new TotemPopCount());
        this.registerModule(new InfChat());
        this.registerModule(new SignSearch());
        this.registerModule(new AntiResourcePack());
        this.registerModule(new AntiKick());
        this.registerModule(new CommandAutoFill());
        this.registerModule(new EntityOwner());
        this.registerModule(new QuakeAimbot());
      

        this.registerModule(new AntiDoubleTap());
        this.registerModule(new ZeroCrystalPlaceDelay());
        this.registerModule(new ClickGUI());
        this.registerModule(new FakePlayer());
        this.registerModule(new AntiShield());
        this.registerModule(new TriggerBot());
        this.registerModule(new CwCrystal());
        this.registerModule(new MarlowCrystal());
        this.registerModule(new AnchorMacro());
        this.registerModule(new SafeAnchor());
        this.registerModule(new AutoLoot());
        this.registerModule(new AutoXp());
        this.registerModule(new AutoInventoryTotem());
        this.registerModule(new Ambience());
        this.registerModule(new CPvpItemRefresh());
        this.registerModule(new NameTags());
        this.registerModule(new AntiTpTrap());
        this.registerModule(new ZeroObiAnchorPlaceDelay());
        this.registerModule(new AutoDtap());
        this.registerModule(new ShowDamageTick());
        this.registerModule(new Capes());
        this.registerModule(new SelfDestruct());
        this.registerModule(new AutoWTap());
        this.registerModule(new CrystalPlaceCheck());
        this.registerModule(new PearlCoords());
        this.registerModule(new MarlowOptimizer());
        this.registerModule(new GhostUse());
        this.registerModule(new RecordPackets());
        this.registerModule(new SkeletonESP());
    }

    private void loadPersistance() {
        if (Persistence.loadConfig()) return;

        // It must be a new config.

        // Auto enable those that should be auto-enabled.
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            
            if (module.shouldAutoEnable()) module.enable();
        }
    }

    public void initialise() {
        toKill.clear();
        eventManager = new EventManager();
        log("Loading Walksy Client...");

        // TODO add persistance for the general config.
        // General config
        this.config = new GeneralConfig();

        // Set up the chat command system
        this.commandHandler = new CommandHandler(ThirdPartyUtils.isOtherClientLoaded() ? config.alterativeCommandPrefix : config.commandPrefix);

        this.registerModules();

        // Load the persistence
        this.loadPersistance();

        // Register commands
        this.registerCommands();
        this.registerModuleCommands();
        eventManager = new EventManager();
        crystalDataTracker = new CrystalDataTrackerUtils();
        rotator = new Rotator();



        // Load all of the block ids
        BlockUtils.initialiseIdList();

        // Generate textRenderer
        this.updateFont(config.font);


        // Done!
        log("Walksy Client loaded!");
    }

    public Rotator getRotator() {
        return rotator;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public CrystalDataTrackerUtils getCrystalDataTracker() {
            return crystalDataTracker;
    }



    /**
     * Registers a given module
     * @param module The module to register
     * @return if the module was registered
     */
    public boolean registerModule(Module module) {
        String key = module.getName().toLowerCase();

        // Check if the module is already registered.
        if (modules.containsKey(key)) {
            log("Module " + key + " is already registered!");
            return false;
        }

        // Add the module
        this.modules.put(key, module);

        return true;
    }
}
