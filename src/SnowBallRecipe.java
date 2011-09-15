import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<ul> ingredients;
    private ul result;

    public SnowBallRecipe(ul result, List<ul> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<ul> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ul> ingredients) {
        this.ingredients = ingredients;
    }

    public ul getResult() {
        return result;
    }

    public void setResult(ul result) {
        this.result = result;
    }

    public void addIngredient(ul ingredient) {
        if(ingredient!=null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
