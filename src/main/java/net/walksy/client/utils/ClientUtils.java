package net.walksy.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IClientWorld;
import net.walksy.client.interfaces.mixin.IEntity;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.Language;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

public class ClientUtils {

    public static double frameTime;
    public static boolean isInStorage() {
        Screen screen = WalksyClient.getClient().currentScreen;
        if (screen == null) return false;

        switch (screen.getClass().getSimpleName()) {
            case "ShulkerBoxScreen":
            case "GenericContainerScreen": return true;
            default: return false;
        }
    
    }

    public static boolean hasElytraEquipt() {
        ItemStack chestSlot = WalksyClient.me().getEquippedStack(EquipmentSlot.CHEST);
		return (chestSlot.getItem() == Items.ELYTRA);
    }

    public static void applyRotation(RotationUtils.Rotation rot) {
        WalksyClient.me().setYaw((float)rot.yaw);
        WalksyClient.me().setPitch((float)rot.pitch);
    }

    public static void lookAtPos(Vec3d pos) {
        applyRotation(
            RotationUtils.getRequiredRotation(pos)
        );
    }

    public static void hitEntity(Entity target) {
        WalksyClient.getClient().interactionManager.attackEntity(WalksyClient.me(), target);
        WalksyClient.me().swingHand(Hand.MAIN_HAND);
    }

    public static RotationUtils.Rotation getRotation() {
        return new RotationUtils.Rotation(
            (double) WalksyClient.me().getYaw(),
            (double) WalksyClient.me().getPitch()
        );
    }

    public static void sendPos(double x, double y, double z, boolean onGround) {
        WalksyClient.me().networkHandler.sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround)
        );
    }

    public static void sendPos(Vec3d pos, boolean onGround) {
        sendPos(pos.x, pos.y, pos.z, onGround);
    }

    public static Boolean inGame() {
        return WalksyClient.me() != null && WalksyClient.getClient().getNetworkHandler() != null;
    }

    public static Boolean isInNetherPortal() {
        IEntity me = (IEntity) WalksyClient.me();
        
        return me.getInNetherPortal();
    }

    public static Boolean isThirdperson() {
        return WalksyClient.getClient().gameRenderer.getCamera().isThirdPerson();
    }

    public static void openChatScreen(String text) {
        if (!inGame()) return;

        WalksyClient.getClient().setScreen(new ChatScreen(text));
    }

    public static void openChatScreen() {
        openChatScreen("");
    }

    public static ItemStack getHandlerSlot(int i) {
        return WalksyClient.me().currentScreenHandler.getSlot(i).getStack();
    }

    public static Integer getPing() {
        ClientPlayNetworkHandler lv = WalksyClient.me().networkHandler;
        PlayerListEntry entry = lv.getPlayerListEntry(WalksyClient.me().getUuid());
        
        if (entry == null) return 0;

        return entry.getLatency();
    }

    public static String getGameModeName() {
        if (WalksyClient.me().isSpectator()) return "Spectator";
        if (WalksyClient.me().isCreative()) return "Creative";

        return "Survival";
    }

    public static Vec3d getControlVelocity(Entity ent, Double speed, Boolean allowFlight) {
        // Initialize as still, or somewhat still.
        Vec3d velocity = new Vec3d(0, allowFlight ? 0 : ent.getVelocity().getY(), 0);

        // We only need these two velocities since the other you can calculate just by multiplying these out by -1 :P
        Vec3d forward = MathsUtils.getForwardVelocity(ent);
        Vec3d right   = MathsUtils.getRightVelocity(ent);

        // Forward + Back
        if (WalksyClient.me().input.pressingForward) velocity = velocity.add(forward.multiply(new Vec3d(speed, 0, speed)));
        if (WalksyClient.me().input.pressingBack)    velocity = velocity.add(forward.multiply(new Vec3d(-speed, 0, -speed)));

        // Right + Left
        if (WalksyClient.me().input.pressingRight) velocity = velocity.add(right.multiply(new Vec3d(speed, 0, speed)));
        if (WalksyClient.me().input.pressingLeft)  velocity = velocity.add(right.multiply(new Vec3d(-speed, 0, -speed)));

        if (allowFlight) {
            // Up + Down
            if (WalksyClient.me().input.jumping)  velocity = velocity.add(0, speed, 0);
            if (WalksyClient.me().input.sneaking) velocity = velocity.add(0, -speed, 0);
        }

        // Set the velocity
        return velocity;
    }

    public static void entitySpeedControl(Entity ent, Double speed, Boolean allowFlight) {
        // Set the velocity
        ent.setVelocity(getControlVelocity(ent, speed, allowFlight));
    }

    public static Vec2f getMousePosition() {
        MinecraftClient client = WalksyClient.getClient();
        Mouse mouse = client.mouse;
        Window window = client.getWindow();

        if (mouse.isCursorLocked()) return new Vec2f(window.getScaledWidth()/2, window.getScaledHeight()/2);

        double scaleFactor = window.getScaleFactor();
        int posX = (int)(mouse.getX()/scaleFactor);
        int posY = (int)(mouse.getY()/scaleFactor);

        return new Vec2f(posX, posY);
    }

    public static String getTextString(Text text) {
        if (text instanceof TranslatableTextContent) {
            Language language = Language.getInstance();

            TranslatableTextContent transText = (TranslatableTextContent)text;

            String str = language.get(transText.getKey());
            return str.equals("%s") ? transText.getKey() : str;
        }

        return text.getString();
    }

    /**
     * Checks to see if an entity can see a given position
     * @param entity
     */
    public static boolean canSee(Entity ent, Vec3d pos, float tickDelta) {
        return ent.world.raycast(
            new RaycastContext(
                ent.getCameraPosVec(tickDelta), // My position
                pos, // The position to check
                RaycastContext.ShapeType.COLLIDER, // The shape type
                RaycastContext.FluidHandling.NONE, // The fluid handling
                ent // The entity that is doing the checking
            )
        ).getType() == HitResult.Type.MISS;
    }

    /**
     * Checks to see if an entity can see a given position
     * @param entity
     */
    public static boolean canSee(Entity ent, Vec3d pos) {
        return canSee(ent, pos, 1);
    }

    /**
     * Checks to see if an entity can see a given position
     * @param entity
     */
    public static boolean canSee(Vec3d pos) {
        return canSee(WalksyClient.me(), pos, 1);
    }

    public static void disconnect(Screen prev) {
        MinecraftClient client = WalksyClient.getClient();
        
        client.world.disconnect();
        client.setScreen(prev);
    }

    public static void openInventory() {
        WalksyClient.getClient().setScreen(new InventoryScreen(WalksyClient.me()));
    }

    public static void refreshInventory() {
        MinecraftClient client = WalksyClient.getClient();

        Boolean wasMouseLocked = client.mouse.isCursorLocked();

        ClientUtils.openInventory();
        client.currentScreen = null;
        
        if (wasMouseLocked) client.mouse.lockCursor();
    }

    /**
     * 
     * @return the username of the player
     */
    public static String getUsername() {
        return WalksyClient.me().getName().getString();
    }

    private static HashMap<UUID, String> usernames = new HashMap<>();
    private static class UsernameResponse {
        String name;
    }
    public static String getPlayerUsername(UUID uuid) {
        if (usernames.containsKey(uuid)) return usernames.get(uuid);

        PlayerEntity player = WalksyClient.getClient().world.getPlayerByUuid(uuid);
        if (player != null) {
            String name = player.getEntityName();

            usernames.put(uuid, name);
            return name;
        }

        usernames.put(uuid, "N/A");

        // Pain.
        Thread thread = new Thread(() -> {
            String data = WebUtils.getJSON("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names", 60000);

            Gson gson = new Gson();
            ArrayList<UsernameResponse> d = gson.fromJson(data, new TypeToken<ArrayList<UsernameResponse>>() {}.getType());

            for (UsernameResponse u : d) {
                usernames.put(uuid, u.name);
                return;
            }
        });
        thread.start();

        return usernames.get(uuid);
    }

    public static GameMode getGameMode(PlayerEntity player) {
        PlayerListEntry playerListEntry = WalksyClient.getClient().getNetworkHandler().getPlayerListEntry(player.getGameProfile().getId());

        // Check for null and return the default gamemode
        if (playerListEntry == null) {
            return GameMode.SURVIVAL;
        }

        return playerListEntry.getGameMode();
    }

    public static GameMode getGameMode() {
        // Get the localplayer in the tab list
        return getGameMode(WalksyClient.me());
    }

    public static int getHealth(LivingEntity ent) {
        return (int)Math.ceil(ent.getHealth());
    }

    public static String getKeyCodeName(int keyCode, int scanCode) {
        return getTextString(InputUtil.fromKeyCode(keyCode, scanCode).getLocalizedText());
    }

    public static String getKeyCodeName(int keyCode) {
        return getKeyCodeName(keyCode, GLFW.GLFW_KEY_UNKNOWN);
    }

    /**
     * Gets a class and gets the last part of the class name.
     * <p>This is horrible and should be replaced ASAP!</p>
     * @param classId The class to get the name of
     * @return The last part of the class name
     */
    private static String getNameFromClassId(String classId) {
        String parts[] = classId.split("\\.");

        if (parts.length == 0) {
            return "";
        }

        return parts[parts.length - 1];
    }

    /**
     * Gets an entity type as a string
     * @param entity The entity to get the type of
     * @return The entity type
     */
    public static String getEntityType(Entity entity) {
        return getNameFromClassId(entity.getType().getTranslationKey());
    }

    /**
     * Gets an effect type as a string
     * @param effect The effect to get the type of
     * @return The effect type
     */
    public static String getEffectType(StatusEffectInstance effect) {
        return getNameFromClassId(effect.getTranslationKey());
    }

    /**
     * Gets the PendingUpdateManager
     * @param world The world to get the manager from
     * @return The PendingUpdateManager
     */
    public static PendingUpdateManager getUpdateManager(ClientWorld world) {
        IClientWorld iWorld = (IClientWorld)world;

        return iWorld.obtainPendingUpdateManager();
    }

    /**
     * Opens, increments and closes the PendingUpdateManager for a given world
     * @param world The world to open the manager for
     * @return The new sequence number
     */
    public static int incrementPendingUpdateManager(ClientWorld world) {
        PendingUpdateManager manager = getUpdateManager(world);

        int current = manager.getSequence();
        manager.close();

        return current;
    }
}
