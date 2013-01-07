/*
 * Copyright (c) 2012 GuntherDW
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

import net.minecraft.client.Minecraft;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GuntherDW
 */
public class mod_SnowBalls extends BaseMod implements dzHookable /*implements ChatHookable*/ {

    public List<SnowBallRecipe> ShapelessRecipes;
    public List<SnowBallShapedRecipe> ShapedRecipes;
    // public static Pattern commandpattern = Pattern.compile("§7§3§3§7([^|]*)\\|?(.*)");
    private HashMap<Integer, Integer> originalStackSizes = new HashMap<Integer, Integer>();
    public static Pattern lineSplitterPattern = Pattern.compile("([^|]*)\\|?(.*)");
    private static Minecraft minecraft;
    private final String snowballsPluginMessageChannel = "SnowBalls";
    private boolean loadedrecipes = false;
    private boolean shownWarning = false;

    @MLProp
    private boolean DEBUG = false;

    public mod_SnowBalls() {
        ShapelessRecipes = new ArrayList<SnowBallRecipe>();
        ShapedRecipes = new ArrayList<SnowBallShapedRecipe>();
        minecraft = ModLoader.getMinecraftInstance();
        dzHooksManager.registerHook(this, snowballsPluginMessageChannel);

        // ChatEvent.handlers.register(new mod_SnowBallsChatListener(), Order.Default);

        // ChatHook.addHook(this);
        // ModLoader.SetInGameHook(this, true, false);
        // this.setItemMaxStack(67, 64); // sign
    }

    @Override
    public String getVersion() {
        return "v0.8a for f1.4.6 SUIv2 GuntherDW, sk89q, lawhran";
    }

    @Override
    public void load() {

    }

    private void log(String message) {
        System.out.println("[mod_SnowBalls] " + message);
        if (minecraft != null && minecraft.v.b() != null)
            minecraft.v.b().a("[mod_SnowBalls] §6" + message);
    }

    @Override
    public void clientDisconnect(ayh netHandler) {
        // System.out.println("serverDisconnect");
        log("Resetting stacksizes for items!");
        for (Map.Entry<Integer, Integer> entry : originalStackSizes.entrySet()) {
            try {
                uk.e[entry.getKey()].d(entry.getValue());
            } catch (ArrayIndexOutOfBoundsException ex) {
                return;
            }
        }
    }

    private void loadShapeLessRecipes() {
        if (!loadedrecipes) {
            for (SnowBallRecipe sr : ShapelessRecipes) {
                ModLoader.addShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
            }
        }
        loadedrecipes = true;
    }

    private void setItemMaxStack(Integer itemId, int maxstack) {
        if (maxstack < 0) maxstack = 1;
        if (itemId > 1024 || itemId == null) return;
        if (DEBUG)
            log("§6" + itemId + " : " + maxstack);
        try {
            if (!originalStackSizes.containsKey(itemId))
                originalStackSizes.put(itemId, uk.e[itemId].m());

            uk.e[itemId].d(maxstack);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return;
        }
    }

    private void injectShapelessRecipe(SnowBallRecipe sr) {
        ModLoader.addShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
    }

    private void injectShapedRecipe(SnowBallShapedRecipe shr) {
        ModLoader.addRecipe(shr.getResult(), shr.generateRecipeLine());
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
        List<ur> recipe = new ArrayList<ur>();
        ur ResultItemStack = null;
        ur tempstack = null;
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
            ResultItemStack = new ur(Iid, Iam, Idmg);
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
                    tempstack = new ur(tid, tam, tdmg);
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
                        tempstack = new ur(tid, tam, tdmg);
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
            System.out.println("NumberFormatException!");
            ex.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("ArrayIndexOutOfBoundsException!");
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void receivePacket(di var1) {
        Set<String> recipeLines = new HashSet<String>();
        String recipeLine = "";
        for (byte b1 : var1.c) {
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

    public di getRegisterPacket() {
        di registerPacket = new di();
        registerPacket.a = snowballsPluginMessageChannel;
        registerPacket.c = new byte[1];
        registerPacket.c[0] = (byte) 26;
        registerPacket.b = 1;
        return registerPacket;
    }
}
