/*
 * Copyright (c) 2012-2015 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package be.guntherdw.minecraft.snowballsclient;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.apache.logging.log4j.core.Logger;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GuntherDW
 */
public class LiteModSnowBalls implements JoinGameListener, PluginChannelListener, Tickable {

    public List<SnowBallRecipe> ShapelessRecipes;
    public List<SnowBallShapedRecipe> ShapedRecipes;
    // public static Pattern commandpattern = Pattern.compile("§7§3§3§7([^|]*)\\|?(.*)");
    private HashMap<Integer, Integer> originalStackSizes = new HashMap<Integer, Integer>();
    public static Pattern lineSplitterPattern = Pattern.compile("([^|]*)\\|?(.*)");
    private static Minecraft minecraft;
    private final String snowballsPluginMessageChannel = "SnowBalls";
    private boolean loadedrecipes = false;
    private boolean shownWarning = false;

    private final static int DELAYED_HELO_TICKS = 10;
    private int delayedHelo = 0;
    private boolean sendRegisterPacket = false;

    Logger log = LiteLoaderLogger.getLogger();

    private boolean DEBUG = false;

    public LiteModSnowBalls() {
        ShapelessRecipes = new ArrayList<SnowBallRecipe>();
        ShapedRecipes = new ArrayList<SnowBallShapedRecipe>();
        minecraft = Minecraft.getMinecraft();
    }

    @Override
    public String getVersion() {
        return "v0.9";
    }

    /**
     * Do startup stuff here, minecraft is not fully initialised when this function is called so mods *must not*
     * interact with minecraft in any way here
     *
     * @param configPath Configuration path to use
     */
    @Override
    public void init(File configPath) {

    }

    /**
     * Called when the loader detects that a version change has happened since this mod was last loaded
     *
     * @param version       new version
     * @param configPath    Path for the new version-specific config
     * @param oldConfigPath Path for the old version-specific config
     */
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    private void log(String message) {
        log.info("[mod_SnowBalls] " + message);
        if (minecraft != null && minecraft.ingameGUI.getChatGUI() != null)
            minecraft.ingameGUI.getChatGUI().addToSentMessages("[mod_SnowBalls] §6" + message);
    }

    private void loadShapeLessRecipes() {
        if (!loadedrecipes) {
            for (SnowBallRecipe sr : ShapelessRecipes) {
                CraftingManager.getInstance().addShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
            }
        }
        loadedrecipes = true;
    }

    private void setItemMaxStack(Integer itemId, int maxstack) {
        if (maxstack < 0) maxstack = 1;
        if (itemId < 1 || itemId > 4096) return;
        if (DEBUG)
            log("§6" + itemId + " : " + maxstack);
        try {
            if (!originalStackSizes.containsKey(itemId))
                originalStackSizes.put(itemId, Item.getItemById(itemId).getItemStackLimit());

            Item.getItemById(itemId).setMaxStackSize(maxstack);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return;
        }
    }

    private void injectShapelessRecipe(SnowBallRecipe sr) {
        CraftingManager.getInstance().addShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
    }

    private void injectShapedRecipe(SnowBallShapedRecipe shr) {
        CraftingManager.getInstance().addRecipe(shr.getResult(), shr.generateRecipeLine());
    }

    private boolean addItems(String[] items) {
        for (String itemstring : items) {
            String[] split = itemstring.split(";");
            Integer itemId = null;
            Integer maxStack = null;
            try {
                itemId = Integer.parseInt(split[0]);
                maxStack = Integer.parseInt(split[1]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            if (maxStack == null) maxStack = 64;
            if (itemId != null) this.setItemMaxStack(itemId, maxStack);
            // System.out.println(itemId+":"+maxStack);
        }
        return true;
    }

    private boolean parseLine(String result, String[] args, boolean inject) {
        String[] res = result.split(":");
        try {
            String modus = res[0];
            if (DEBUG)
                log("§4SnowBalls-client §6" + result);
            if (modus.equals("i")) {
                return this.addItems(args);
            } else if (modus.equals("1") || modus.equals("0")) {
                return this.addRecipe(result, args, inject);
            } else {
                if (!shownWarning) {
                    shownWarning = true;
                    log("error");
                    log("§6please make sure you are running");
                    log("§6the latest version!");
                }

                return true;
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean addRecipe(String result, String[] args, boolean inject) {
        /* System.out.println("SneuwSUI: " + result); */
        inject = true;
        Integer Iid, Idmg, Iam;
        Integer tid, tdmg, tam;
        List<ItemStack> recipe = new ArrayList<ItemStack>();
        ItemStack ResultItemStack = null;
        ItemStack tempstack = null;
        String[] res = result.split(":");
        Integer type = null;
        String resu = "";
        try {
            type = Integer.parseInt(res[0]);
            resu = res[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            resu = result;
        } catch (NumberFormatException ex) {
            resu = result;
        }


        if (args.length == 0)
            return false;

        try {
            String[] resultitem = resu.split(";");
            Iid = Integer.parseInt(resultitem[0]);
            Idmg = Integer.parseInt(resultitem[1]);
            Iam = Integer.parseInt(resultitem[2]);
            ResultItemStack = new ItemStack(Item.getItemById(Iid), Iam, Idmg);
            // System.out.println("Adding recipe for "+Iam+" "+ResultItemStack.l());
            /**
             * 0 or nothing (older format) : ShapeLess recipe
             * 1                           : Shaped    recipe
             */
            if (type == null || type == 0) {
                for (String a : args) {
                    String[] ra = a.split(";");
                    tid = Integer.parseInt(ra[0]);
                    tdmg = ra.length > 1 ? Integer.parseInt(ra[1]) : 0;
                    tam = ra.length > 2 ? Integer.parseInt(ra[2]) : 1;
                    tempstack = new ItemStack(Item.getItemById(tid), tam, tdmg);
                    recipe.add(tempstack);
                }
                SnowBallRecipe shapelessRecipe = new SnowBallRecipe(ResultItemStack, recipe);
                ShapelessRecipes.add(shapelessRecipe);
                if (inject)
                    this.injectShapelessRecipe(shapelessRecipe);
            } else {
                int pos = 0;
                SnowBallShapedRecipe shr = new SnowBallShapedRecipe(ResultItemStack);
                for (String a : args) {

                    if ((!a.trim().equals(""))) {
                        String[] ra = a.split(";");
                        tid = Integer.parseInt(ra[0]);
                        tdmg = ra.length > 1 ? Integer.parseInt(ra[1]) : 0;
                        tam = ra.length > 2 ? Integer.parseInt(ra[2]) : 1;
                        tempstack = new ItemStack(Item.getItemById(tid), tam, tdmg);
                        if (pos < 9) {
                            shr.setIngredientSpot(pos, tempstack);
                        }
                    }
                    pos++;
                }
                ShapedRecipes.add(shr);
                if (inject)
                    this.injectShapedRecipe(shr);
            }
        } catch (NumberFormatException ex) {
            log.warn("NumberFormatException!");
            ex.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            log.warn("ArrayIndexOutOfBoundsException!");
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Called when a custom payload packet arrives on a channel this mod has registered
     *
     * @param channel Channel on which the custom payload was received
     * @param data    Custom payload data
     */
    @Override
    public void onCustomPayload(String channel, PacketBuffer data) {
        log("Got custom payload packet!");
        Set<String> recipeLines = new HashSet<String>();
        String recipeLine = "";
        byte[] bytes = data.array();
        for (byte b1 : bytes) {
            if (b1 == (byte) 0) {
                recipeLines.add(recipeLine);
                recipeLine = "";
            } else {
                recipeLine += (char) b1;
            }
        }

        for (String line : recipeLines) {
            Matcher matcher = lineSplitterPattern.matcher(line);
            if (matcher.find()) {
                parseLine(matcher.group(1), matcher.group(2).split("\\|"), true);
            }
        }
    }

    public C17PacketCustomPayload getRegisterPacket() {
        PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
        pb.writeByte((byte) 26);
        C17PacketCustomPayload registerPacket = new C17PacketCustomPayload(snowballsPluginMessageChannel, pb);
        return registerPacket;
    }

    /**
     * Called on join game
     *
     * @param netHandler     Net handler
     * @param joinGamePacket Join game packet
     * @param serverData     ServerData object representing the server being connected to
     * @param realmsServer   If connecting to a realm, a reference to the RealmsServer object
     */
    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {
        sendRegisterPacket = true;
        delayedHelo = DELAYED_HELO_TICKS;
    }

    /**
     * Get the display name
     *
     * @return display name
     */
    @Override
    public String getName() {
        return "SnowBalls";
    }



    /**
     * Return a list of the plugin channels the mod wants to register
     *
     * @return plugin channel names as a list, it is recommended to use {@link ImmutableList#of} for this purpose
     */
    @Override
    public List<String> getChannels() {
        return Arrays.asList(new String[] { snowballsPluginMessageChannel });
    }

    private boolean shouldTick() {
        return LiteLoader.getClientPluginChannels().isRemoteChannelRegistered(snowballsPluginMessageChannel);
    }

    /**
     * Called every frame
     *
     * @param minecraft    Minecraft instance
     * @param partialTicks Partial tick value
     * @param inGame       True if in-game, false if in the menu
     * @param clock        True if this is a new tick, otherwise false if it's a regular frame
     */
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if(!inGame) return;
        if (!shouldTick()) return;

        if (sendRegisterPacket) {
            if (delayedHelo == 0) {
                log("Sending helo packet");
                minecraft.getNetHandler().addToSendQueue(getRegisterPacket());
                sendRegisterPacket = false;
            } else {
                delayedHelo--;
            }
        }
    }
}
