import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GuntherDW
 */
public class mod_SnowBalls extends BaseMod implements ChatHookable {

    public List<SnowBallRecipe> ShapelessRecipes;
    public List<SnowBallShapedRecipe> ShapedRecipes;
    public static Pattern commandpattern = Pattern.compile("§7§3§3§7([^|]*)\\|?(.*)");
    private boolean loadedrecipes = false;
    private boolean shownWarning = false;
    private boolean DEBUG = false;

    public mod_SnowBalls() {
        ShapelessRecipes = new ArrayList<SnowBallRecipe>();
        ShapedRecipes = new ArrayList<SnowBallShapedRecipe>();
        ChatHook.addHook(this);
        ModLoader.SetInGameHook(this, true, false);
        // this.setItemMaxStack(67, 64); // sign
    }

    private void loadShapeLessRecipes() {
        if(!loadedrecipes) {
            for(SnowBallRecipe sr : ShapelessRecipes) {
                ModLoader.AddShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
            }
        }
        loadedrecipes = true;
    }

    private void setItemMaxStack(Integer itemId, int maxstack) {
        if(maxstack<0) maxstack = 1;
        if(itemId>1024 || itemId == null) return;
        if(DEBUG)
            ModLoader.getMinecraftInstance().v.a("§6"+itemId +" : "+maxstack);
        gm item = null;
        try{
            item = (gm) gm.c[itemId];
            if(item!=null) item.d(maxstack);
            else { if(DEBUG) ModLoader.getMinecraftInstance().v.a("§6 WUZ NULL"); }
        } catch(ArrayIndexOutOfBoundsException ex) {
            return;
        }
    }

    private void injectShapeLessRecipe(SnowBallRecipe sr) {
        /* String s = "";
        for(iw ing : sr.getIngredients()) {
            s+=ing.l()+"*"+ing.a+",";
        }
        if(s.length()>0)
            s=s.substring(0, s.length()-1);
        System.out.println("Injecting recipe : "+s+" = "+ sr.getResult().a +"*"+ sr.getResult().l()); */
        ModLoader.AddShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
    }

    private void injectShapedRecipe(SnowBallShapedRecipe shr) {
        /* String s = "";
        for(iw ing : shr.getIngredients().values()) {
            s+=ing.l()+"*"+ing.a+",";
        }
        if(s.length()>0)
            s=s.substring(0, s.length()-1);
        System.out.println("Injecting shaped recipe : "+s+" = "+ shr.getResult().a +"*"+ shr.getResult().l()); */
        // this.a(new iw(gk.bb, 1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), gk.aI, Character.valueOf('X'), gk.aO});

        ModLoader.AddRecipe(shr.getResult(), shr.generateRecipeLine());
    }

    @Override
    public String Version() {
        return "1.7.3 GuntherDW, sk89q, lawhran";
    }

    private boolean addItems(String[] items) {
        for(String itemstring : items)
        {
            String[] split = itemstring.split(";");
            Integer itemId = null;
            Integer maxStack = null;
            try{
                itemId = Integer.parseInt(split[0]);
                maxStack = Integer.parseInt(split[1]);
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            } catch(ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            if(maxStack==null) maxStack = 64;
            if(itemId!=null) this.setItemMaxStack(itemId, maxStack);
            System.out.println(itemId+":"+maxStack);
        }
        return true;
    }

    private boolean parseLine(String result, String[] args, boolean inject) {
        String[] res = result.split(":");
        try{
            String modus = res[0];
            if(DEBUG)
                ModLoader.getMinecraftInstance().v.a("§4SnowBalls-client §6"+result);
            if(modus.equals("i")) {
                return this.addItems(args);
            } else if (modus.equals("1") || modus.equals("0")) {
                return this.addRecipe(result, args, inject);
            } else {
                if(!shownWarning) {
                    shownWarning=true;
                    // ModLoader.getMinecraftInstance().
                    ModLoader.getMinecraftInstance().v.a("§4Snowballs-client error");
                    ModLoader.getMinecraftInstance().v.a("§4Snowballs-client §6please make sure you are running");
                    ModLoader.getMinecraftInstance().v.a("§4SnowBalls-client §6the latest version!");
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
        List<iz> recipe = new ArrayList<iz>();
        iz ResultItemStack = null;
        iz tempstack = null;
        String[] res = result.split(":");
        Integer type = null;
        String  resu = "";
        try {
            type = Integer.parseInt(res[0]);
            resu = res[1];
        } catch(ArrayIndexOutOfBoundsException ex) {
            resu = result;
        } catch(NumberFormatException ex) {
            resu = result;
        }


        if(args.length==0)
            return false;

        try{
            String[] resultitem = resu.split(";");
            Iid = Integer.parseInt(resultitem[0]);
            Idmg = Integer.parseInt(resultitem[1]);
            Iam = Integer.parseInt(resultitem[2]);
            ResultItemStack = new iz(Iid, Iam, Idmg);
            // System.out.println("Adding recipe for "+Iam+" "+ResultItemStack.l());
            /**
             * 0 or nothing (older format) : ShapeLess recipe
             * 1                           : Shaped    recipe
             */
            if(type == null || type == 0) {
                for(String a : args) {
                    String[] ra = a.split(";");
                    tid = Integer.parseInt(ra[0]);
                    tdmg = Integer.parseInt(ra[1]);
                    tam = Integer.parseInt(ra[2]);
                    tempstack = new iz(tid, tam, tdmg);
                    recipe.add(tempstack);
                }
                SnowBallRecipe sr = new SnowBallRecipe(ResultItemStack, recipe);
                ShapelessRecipes.add(sr);
                if(inject)
                    this.injectShapeLessRecipe(sr);
            } else {
                int pos = 0;
                SnowBallShapedRecipe shr = new SnowBallShapedRecipe(ResultItemStack);
                for(String a : args) {

                    if((!a.trim().equals(""))) {
                        String[] ra = a.split(";");
                        tid = Integer.parseInt(ra[0]);
                        tdmg = Integer.parseInt(ra[1]);
                        tam = Integer.parseInt(ra[2]);
                        tempstack = new iz(tid, tam, tdmg);
                        if(pos<9) {
                            shr.setIngredientSpot(pos, tempstack);
                        }
                    }
                    pos++;
                }
                ShapedRecipes.add(shr);
                if(inject)
                    this.injectShapedRecipe(shr);
            }
        } catch(NumberFormatException ex) {
            System.out.println("NumberFormatException!");
            ex.printStackTrace();
            return false;
        } catch(ArrayIndexOutOfBoundsException ex) {
            System.out.println("ArrayIndexOutOfBoundsException!");
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean processChat(String chat) {
        Matcher matcher = commandpattern.matcher(chat);
        if(matcher.find()) {
            if(matcher.group(1).equals("")) {
                if(ModLoader.getMinecraftInstance().l()) {
                    ModLoader.getMinecraftInstance().h.a("/snowballs client");
                }
                return true;
            } else {
                return parseLine(matcher.group(1), matcher.group(2).split("\\|"), true);
            }
        } else {
            return false;
        }
    }
}
