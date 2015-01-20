package be.guntherdw.minecraft.snowballsclient;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author guntherdw
 */
public class SearchRenderer {

    private LiteModSnowBalls modInstance;
    private Set<BlockPos> foundPositions;
    private Set<BlockModel> models;
    private boolean enabled;

    public SearchRenderer(LiteModSnowBalls instance) {
        this.modInstance = instance;
        this.enabled = true;
        foundPositions = new HashSet<BlockPos>();
        models = new HashSet<BlockModel>();
    }

    public void decodePayload(byte[] data) {
        this.clearEntities();
        StringBuilder sb = new StringBuilder();
        for(byte b1 : data) {
            if(b1 == 0) {
                String line = sb.toString();
                String[] coords = line.split(",");

                BlockPos blockPos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                foundPositions.add(blockPos);
                models.add(new BlockModel(blockPos));
                sb = new StringBuilder();
            } else {
                sb.append((char)b1);
            }
        }
    }

    private double getPlayerX(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return p.prevPosX + (p.posX - p.prevPosX) * partialTicks;
    }

    private double getPlayerY(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return p.prevPosY + (p.posY - p.prevPosY) * partialTicks;
    }

    private double getPlayerZ(float partialTicks) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return p.prevPosZ + (p.posZ - p.prevPosZ) * partialTicks;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void clearEntities() {
        foundPositions.clear();
        models.clear();
    }

    public void onPostRenderEntities(float partialTicks) {
        if(this.enabled && foundPositions.size() > 0) {
            RenderHelper.disableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_FOG);

            GL11.glPushMatrix();

            GL11.glTranslated(
                -getPlayerX(partialTicks),
                -getPlayerY(partialTicks),
                -getPlayerZ(partialTicks));

            /* Draw blocks */

            for(BlockModel model : models) {
                model.render();
            }

            /* End draw blocks */

            GL11.glPopMatrix();

            if (foggy) {
                GL11.glEnable(GL11.GL_FOG);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);

            RenderHelper.enableStandardItemLighting();
        }
    }

    public class BlockModel {


        private double x, y, z;
        private float lineWidth;
        private int red, green, blue, alpha;

        public BlockModel(BlockPos blockPos) {
            this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        // bounds: [0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375]

        public BlockModel(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;

            lineWidth = 3.0f;
            red = 85;
            green = 228;
            blue = 255;
            alpha = 100;
        }



        public void render() {

            double startingPointX = x;
            double startingPointY = y;
            double startingPointZ = z;
            
            double width = 1.0D;
            double height = 1.0D;
            double girth = 1.0D;

            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();

            wr.startDrawing(GL11.GL_LINES);
            wr.setColorRGBA(red, green, blue, alpha);
            GL11.glLineWidth(lineWidth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);

            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);


            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);


            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);


            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            tess.draw();
        }
    }

}
