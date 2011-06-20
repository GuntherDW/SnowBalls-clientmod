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

    public mod_SnowBalls() {
        ShapelessRecipes = new ArrayList<SnowBallRecipe>();
        ShapedRecipes = new ArrayList<SnowBallShapedRecipe>();
        ChatHook.addHook(this);
        ModLoader.SetInGameHook(this, true, false);
    }

    private void loadShapeLessRecipes() {
        if(!loadedrecipes) {
            for(SnowBallRecipe sr : ShapelessRecipes) {
                ModLoader.AddShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
            }
        }
        loadedrecipes = true;
    }

    private void injectShapeLessRecipe(SnowBallRecipe sr) {
        String s = "";
        for(iw ing : sr.getIngredients()) {
            s+=ing.l()+"*"+ing.a+",";
        }
        if(s.length()>0)
            s=s.substring(0, s.length()-1);
        // System.out.println("Injecting recipe : "+s+" = "+ sr.getResult().a +"*"+ sr.getResult().l());
        ModLoader.AddShapelessRecipe(sr.getResult(), sr.getIngredients().toArray());
    }

    private void injectShapedRecipe(SnowBallShapedRecipe shr) {
        String s = "";
        ModLoader.AddRecipe(shr.getResult(), shr.getIngredients());
    }

    @Override
    public String Version() {
        return "1.6.6 GuntherDW, sk89q, lawhran";
    }

    private boolean addRecipe(String result, String[] args, boolean inject) {
        // System.out.println("SneuwSUI: " + type);
        inject = true;
        Integer Iid, Idmg, Iam;
        Integer tid, tdmg, tam;
        List<iw> recipe = new ArrayList<iw>();
        iw ResultItemStack = null;
        iw tempstack = null;
        String[] res = result.split(":");
        Boolean type = null;
        String  resu = "";
        try {
            type = Boolean.parseBoolean(res[0]);
            resu = res[1];
        } catch(ArrayIndexOutOfBoundsException ex) {
            resu = result;
        }


        if(args.length==0)
            return false;

        try{
            String[] resultitem = resu.split(";");
            Iid = Integer.parseInt(resultitem[0]);
            Idmg = Integer.parseInt(resultitem[1]);
            Iam = Integer.parseInt(resultitem[2]);
            ResultItemStack = new iw(Iid, Iam, Idmg);
            System.out.println("Adding recipe for "+Iam+" "+ResultItemStack.l());
            /**
             * 0 or nothing (older format) : ShapeLess recipe
             * 1                           : Shaped    recipe
             */
            if(type == null || type == false) {
                for(String a : args) {
                    String[] ra = a.split(";");
                    tid = Integer.parseInt(ra[0]);
                    tdmg = Integer.parseInt(ra[1]);
                    tam = Integer.parseInt(ra[2]);
                    tempstack = new iw(tid, tam, tdmg);
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

                    if(a!="") {
                        String[] ra = a.split(";");
                        tid = Integer.parseInt(ra[0]);
                        tdmg = Integer.parseInt(ra[1]);
                        tam = Integer.parseInt(ra[2]);
                        tempstack = new iw(tid, tam, tdmg);
                        if(pos<9) {
                            shr.setIngredientSpot(pos, tempstack);
                        }
                    }
                    pos++;
                }
                ShapedRecipes.add(shr);
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
        // System.out.println("Sneuw: " + chat);
        if(matcher.find()) {
            // System.out.println("\'" + matcher.group(1) + "\'  \'" + matcher.group(2) + "\'");
            if(matcher.group(1).equals("")) {
                if(ModLoader.getMinecraftInstance().l()) {
                    // System.out.println("Sending modloader snowballs...");
                    ModLoader.getMinecraftInstance().h.a("/snowballs client");
                } else {
                }

                return true;
            } else {
                return addRecipe(matcher.group(1), matcher.group(2).split("\\|"), true);
            }
        } else {
            return false;
        }
    }
}
