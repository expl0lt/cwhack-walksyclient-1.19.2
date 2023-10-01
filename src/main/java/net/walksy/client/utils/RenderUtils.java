package net.walksy.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.walksy.client.WalksyClient;
import net.walksy.client.interfaces.mixin.IItemRenderer;
import net.walksy.client.misc.Colour;
import net.walksy.client.mixin.render.WorldRendererAccessor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;

import java.awt.Color;

public class RenderUtils {

	// TODO Cache this or remove it, this is for my dumb brain and debugging reasons
	public static void g11COLORRGB(float r, float g, float b, float a) {
		GL11.glColor4f(r/255f, g/255f, b/255f, a/255f);
	}

	private static final Box DEFAULT_BOX = new Box(0, 0, 0, 1, 1, 1);

	public static Vec3d getEntityRenderPosition(Entity entity, double partial) {
		double x = entity.prevX + ((entity.getX() - entity.prevX) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().x;
		double y = entity.prevY + ((entity.getY() - entity.prevY) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().y;
		double z = entity.prevZ + ((entity.getZ() - entity.prevZ) * partial) - WalksyClient.getClient().getEntityRenderDispatcher().camera.getPos().z;
		return new Vec3d(x, y, z);
	}

	public static void bindTexture(Identifier identifier) {
		RenderSystem.setShaderTexture(0, identifier);
	}



	public static int getPercentColor(float percent) {
		if (percent <= 15)
			return new Color(255, 0, 0).getRGB();
		else if (percent <= 25)
			return new Color(255, 75, 92).getRGB();
		else if (percent <= 50)
			return new Color(255, 123, 17).getRGB();
		else if (percent <= 75)
			return new Color(255, 234, 0).getRGB();
		return new Color(0, 255, 0).getRGB();
	}

	public static void drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
		bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
		bufferBuilder.end();
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	public static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, float width, float height, int textureWidth, int textureHeight) {
		drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
		drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(MatrixStack matrices, float x0, float y0, float x1, float y1, int z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
		drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, y0, x1, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
	}

	public static void drawFace(MatrixStack matrixStack, float x, float y, int renderScale, Identifier id) {
		try {
			bindTexture(id);
			drawTexture(matrixStack, x, y, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
			drawTexture(matrixStack, x, y, 8 * renderScale, 8 * renderScale, 40 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
		}catch (Exception e){}
	}

	public static boolean isOnScreen2d(Vec3d pos) {
		return pos != null && (pos.z > -1 && pos.z < 1);
	}

	public static Vec3d getPos(Entity entity, float yOffset, float partialTicks, MatrixStack matrixStack) {
		Vec3d bound = getEntityRenderPosition(entity, partialTicks).add(0, yOffset, 0);
		Vector4f vector4f = new Vector4f((float)bound.x, (float)bound.y, (float)bound.z, 1.f);
		vector4f.transform(matrixStack.peek().getPositionMatrix());
		Vec3d twoD = to2D(vector4f.getX(), vector4f.getY(), vector4f.getZ());
		return new Vec3d(twoD.x, twoD.y, twoD.z);
	}

	private static Vec3d to2D(double x, double y, double z) {
		int displayHeight = WalksyClient.getClient().getWindow().getHeight();
		Vector3D screenCoords = new Vector3D();
		int[] viewport = new int[4];
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		Matrix4x4 matrix4x4Proj = Matrix4x4.copyFromColumnMajor(RenderSystem.getProjectionMatrix());
		Matrix4x4 matrix4x4Model = Matrix4x4.copyFromColumnMajor(RenderSystem.getModelViewMatrix());
		matrix4x4Proj.mul(matrix4x4Model).project((float) x, (float) y, (float) z, viewport, screenCoords);

		return new Vec3d(screenCoords.x / getScaleFactor(), (displayHeight - screenCoords.y) / getScaleFactor(), screenCoords.z);
	}

	public static double getScaleFactor() {
		return WalksyClient.getClient().getWindow().getScaleFactor();
	}


	public static void setup3DRender(boolean disableDepth) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		if (disableDepth)
			RenderSystem.disableDepthTest();
		RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
		RenderSystem.enableCull();
	}

	public static Frustum getFrustum() {
		return ((WorldRendererAccessor) WalksyClient.getClient().worldRenderer).getFrustum();
	}



	public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrices = new MatrixStack();

		Camera camera = WalksyClient.getClient().gameRenderer.getCamera();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

		matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrices;
	}

	public static void end3DRender() {
		RenderSystem.enableTexture();
		RenderSystem.disableCull();
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
	}

	public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup3DRender(true);

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Outline
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		RenderSystem.lineWidth(lineWidth);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexBoxLines(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		RenderSystem.enableCull();

		end3DRender();
	}

	public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup3DRender(true);

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Fill
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Vertexer.vertexBoxQuads(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		end3DRender();
	}

	
	public static BlockPos getRegion() {
		BlockPos camPos = RenderUtils.getCameraBlockPos();
        int regionX = (camPos.getX() >> 9) * 512;
        int regionZ = (camPos.getZ() >> 9) * 512;

		return new BlockPos(regionX, 0, regionZ);
	}

	public static VertexBuffer simpleMobBox;
	static {
		simpleMobBox = new VertexBuffer();
		Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb, simpleMobBox);
	}

	public static VertexBuffer blockBox;
	static {
		blockBox = new VertexBuffer();
		Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb, blockBox);
	}

	private static void renderBox(Entity e, double partialTicks, MatrixStack mStack) {
		renderBox(e, partialTicks, mStack, true, 0f);
	}

	public static void renderBox(Entity e, double partialTicks, MatrixStack mStack, boolean blend, float extraSize) {
		// Render Section
		mStack.push();

		// GL Settings
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (blend) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

		// Get region
		BlockPos region = getRegion();

        RenderUtils.applyRegionalRenderOffset(mStack);

        // Load the renderer
        RenderSystem.setShader(GameRenderer::getPositionShader);

        // Translate the point of rendering
        mStack.translate(
            e.prevX + (e.getX() - e.prevX) * partialTicks - region.getX(),
            e.prevY + (e.getY() - e.prevY) * partialTicks,
            e.prevZ + (e.getZ() - e.prevZ) * partialTicks - region.getZ()
        );

        // Update the size of the box.
        mStack.scale(e.getWidth() + extraSize, e.getHeight() + extraSize, e.getWidth() + extraSize);

        // Make the boxes change colour depending on their distance.
        float f = WalksyClient.me().distanceTo(e) / 20F;
        RenderSystem.setShaderColor(2 - f, f, 0, 0.5F);

		// Render the box
		drawOutlinedBox(DEFAULT_BOX, mStack);

        // GL resets
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        if (blend) {
            GL11.glDisable(GL11.GL_BLEND);
        }

		// Pop the stack (i.e. render it)
		mStack.pop();
	}

	public static float normaliseColourPart(float x) {
		return x/255f;
	}

	public static void drawLine3D(MatrixStack matrixStack, Vec3d start, Vec3d end, float r, float g, float b, float a) {
		matrixStack.push();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		RenderUtils.applyRegionalRenderOffset(matrixStack);
		RenderSystem.setShaderColor(normaliseColourPart(r), normaliseColourPart(g), normaliseColourPart(b), normaliseColourPart(a));

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
	
		int regionX = getRegion().getX();
		int regionZ = getRegion().getZ();
		
		bufferBuilder.vertex(matrix, (float)(start.x - regionX), (float)start.y, (float)(start.z - regionZ)).next();
		bufferBuilder.vertex(matrix, (float)end.x - regionX, (float)end.y, (float)end.z - regionZ).next();
		
		BufferRenderer.drawWithShader(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		matrixStack.pop();
	}

	public static void drawTracer(MatrixStack matrixStack, Vec3d end, float delta, float r, float g, float b, float a) {
		RenderUtils.drawLine3D(
			matrixStack,
			RotationUtils.getClientLookVec().add(getCameraPos(delta)),
			end,
			r, g, b, a
		);
	}

	public static void drawTracer(MatrixStack matrixStack, Vec3d end, float delta) {
		drawTracer(matrixStack, end, delta, 255, 255, 255, 255);
	}

	public static void drawTracer(MatrixStack matrixStack, Vec3d end, float delta, Colour colour) {
		drawTracer(matrixStack, end, delta, colour.r, colour.g, colour.b, colour.a);
	}

	public static void scissorBox(int startX, int startY, int endX, int endY)
	{
		int width = endX - startX;
		int height = endY - startY;
		int bottomY = WalksyClient.getClient().currentScreen.height - endY;
		double factor = WalksyClient.getClient().getWindow().getScaleFactor();
		
		int scissorX = (int)(startX * factor);
		int scissorY = (int)(bottomY * factor);
		int scissorWidth = (int)(width * factor);
		int scissorHeight = (int)(height * factor);
		GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
	}
	
	public static void applyRenderOffset(MatrixStack matrixStack)
	{
		applyCameraRotationOnly();
		Vec3d camPos = getCameraPos();
		
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	public static void applyRegionalRenderOffset(MatrixStack matrixStack)
	{
		applyCameraRotationOnly();
		
		Vec3d camPos = getCameraPos();
		BlockPos blockPos = getCameraBlockPos();
		
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;
		
		matrixStack.translate(regionX - camPos.x, -camPos.y,
			regionZ - camPos.z);
	}
	
	public static void applyRegionalRenderOffset(MatrixStack matrixStack,
		Chunk chunk)
	{
		applyCameraRotationOnly();
		
		Vec3d camPos = getCameraPos();
		
		int regionX = (chunk.getPos().getStartX() >> 9) * 512;
		int regionZ = (chunk.getPos().getStartZ() >> 9) * 512;
		
		matrixStack.translate(regionX - camPos.x, -camPos.y,
			regionZ - camPos.z);
	}
	
	public static void applyCameraRotationOnly()
	{
		// no longer necessary for some reason
		
		// Camera camera =
		// CheatClient.getClient().getBlockEntityRenderDispatcher().camera;
		// GL11.glRotated(MathHelper.wrapDegrees(camera.getPitch()), 1, 0, 0);
		// GL11.glRotated(MathHelper.wrapDegrees(camera.getYaw() + 180.0), 0, 1,
		// 0);
	}
	
	public static Vec3d getCameraPos(float delta) {
		Entity ent = WalksyClient.getClient().cameraEntity;

		Vec3d deltaPos = ent.getEyePos().add(
			ent.getPos().multiply(-1)
		);

		return ent.getLerpedPos(delta).add(deltaPos);
	}

	public static Vec3d getCameraPos() {
		return WalksyClient.getClient().getBlockEntityRenderDispatcher().camera.getPos();
	}
	
	public static BlockPos getCameraBlockPos() {
		return WalksyClient.getClient().getBlockEntityRenderDispatcher().camera
			.getBlockPos();
	}
	
	public static void drawSolidBox(MatrixStack matrixStack)
	{
		drawSolidBox(DEFAULT_BOX, matrixStack);
	}
	
	public static void drawSolidBox(Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
			VertexFormats.POSITION);
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	
	public static void drawSolidBox(Box bb, VertexBuffer vertexBuffer)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
			VertexFormats.POSITION);
		drawSolidBox(bb, bufferBuilder);

		vertexBuffer.upload(bufferBuilder.end());
	}
	
	public static void drawSolidBox(Box bb, BufferBuilder bufferBuilder)
	{
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
	}
	
	public static void drawOutlinedBox(MatrixStack matrixStack) {
		drawOutlinedBox(DEFAULT_BOX, matrixStack);
	}
	
	public static void drawOutlinedBox(Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);

		// It was always centered (which makes sense) so we need to shift it so that the bottom of the box isn't in the center
		matrixStack.translate(-0.5f, 0, -0.5f);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		tessellator.draw();
	}
	
	public static void drawOutlinedBox(Box bb, VertexBuffer vertexBuffer) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
		drawOutlinedBox(bb, bufferBuilder);

		vertexBuffer.upload(bufferBuilder.end());
	}
	
	public static void drawOutlinedBox(Box bb, BufferBuilder bufferBuilder)
	{
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
	}
	
	public static void drawCrossBox(Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
			.next();

		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	
	public static void drawCrossBox(Box bb, VertexBuffer vertexBuffer)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		drawCrossBox(bb, bufferBuilder);

		vertexBuffer.upload(bufferBuilder.end());
	}
	
	public static void drawCrossBox(Box bb, BufferBuilder bufferBuilder)
	{
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
		
		bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
	}
	
	public static void drawNode(Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		double midX = (bb.minX + bb.maxX) / 2;
		double midY = (bb.minY + bb.maxY) / 2;
		double midZ = (bb.minZ + bb.maxZ) / 2;
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.maxZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.minX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)bb.minX, (float)midY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.minZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.minZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.maxX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)bb.maxX, (float)midY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.maxZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.maxY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.maxX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.maxY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.minX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.maxY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.minZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.maxY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.maxZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.minY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.maxX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.minY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)bb.minX, (float)midY, (float)midZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.minY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.minZ)
			.next();
		
		bufferBuilder.vertex(matrix, (float)midX, (float)bb.minY, (float)midZ)
			.next();
		bufferBuilder.vertex(matrix, (float)midX, (float)midY, (float)bb.maxZ)
			.next();
		
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	
	public static void drawNode(Box bb, VertexBuffer vertexBuffer)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		drawNode(bb, bufferBuilder);
		
		vertexBuffer.upload(bufferBuilder.end());
	}
	
	public static void drawNode(Box bb, BufferBuilder bufferBuilder)
	{
		double midX = (bb.minX + bb.maxX) / 2;
		double midY = (bb.minY + bb.maxY) / 2;
		double midZ = (bb.minZ + bb.maxZ) / 2;
		
		bufferBuilder.vertex(midX, midY, bb.maxZ).next();
		bufferBuilder.vertex(bb.minX, midY, midZ).next();
		
		bufferBuilder.vertex(bb.minX, midY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.minZ).next();
		
		bufferBuilder.vertex(midX, midY, bb.minZ).next();
		bufferBuilder.vertex(bb.maxX, midY, midZ).next();
		
		bufferBuilder.vertex(bb.maxX, midY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.maxZ).next();
		
		bufferBuilder.vertex(midX, bb.maxY, midZ).next();
		bufferBuilder.vertex(bb.maxX, midY, midZ).next();
		
		bufferBuilder.vertex(midX, bb.maxY, midZ).next();
		bufferBuilder.vertex(bb.minX, midY, midZ).next();
		
		bufferBuilder.vertex(midX, bb.maxY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.minZ).next();
		
		bufferBuilder.vertex(midX, bb.maxY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.maxZ).next();
		
		bufferBuilder.vertex(midX, bb.minY, midZ).next();
		bufferBuilder.vertex(bb.maxX, midY, midZ).next();
		
		bufferBuilder.vertex(midX, bb.minY, midZ).next();
		bufferBuilder.vertex(bb.minX, midY, midZ).next();
		
		bufferBuilder.vertex(midX, bb.minY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.minZ).next();
		
		bufferBuilder.vertex(midX, bb.minY, midZ).next();
		bufferBuilder.vertex(midX, midY, bb.maxZ).next();
	}
	
	public static void drawArrow(Vec3d from, Vec3d to, MatrixStack matrixStack)
	{
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		
		double startX = from.x;
		double startY = from.y;
		double startZ = from.z;
		
		double endX = to.x;
		double endY = to.y;
		double endZ = to.z;
		
		matrixStack.push();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		
		bufferBuilder
			.vertex(matrix, (float)startX, (float)startY, (float)startZ).next();
		bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)endZ)
			.next();
		
		matrixStack.translate(endX, endY, endZ);
		matrixStack.scale(0.1F, 0.1F, 0.1F);
		
		double xDiff = endX - startX;
		double yDiff = endY - startY;
		double zDiff = endZ - startZ;
		
		float xAngle = (float)(Math.atan2(yDiff, -zDiff) + Math.toRadians(90));
		matrixStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(xAngle));
		
		double yzDiff = Math.sqrt(yDiff * yDiff + zDiff * zDiff);
		float zAngle = (float)Math.atan2(xDiff, yzDiff);
		matrixStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(zAngle));
		
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		
		matrixStack.pop();
		
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}
	
	public static void drawArrow(Vec3d from, Vec3d to,
		VertexBuffer vertexBuffer)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		
		drawArrow(from, to, bufferBuilder);
		
		vertexBuffer.upload(bufferBuilder.end());
	}
	
	public static void drawArrow(Vec3d from, Vec3d to,
		BufferBuilder bufferBuilder)
	{
		double startX = from.x;
		double startY = from.y;
		double startZ = from.z;
		
		double endX = to.x;
		double endY = to.y;
		double endZ = to.z;
		
		Matrix4f matrix = new Matrix4f();
		matrix.loadIdentity();
		
		bufferBuilder
			.vertex(matrix, (float)startX, (float)startY, (float)startZ).next();
		bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)endZ)
			.next();
		
		matrix.multiplyByTranslation((float)endX, (float)endY, (float)endZ);
		matrix.multiply(Matrix4f.scale(0.1F, 0.1F, 0.1F));
		
		double xDiff = endX - startX;
		double yDiff = endY - startY;
		double zDiff = endZ - startZ;
		
		float xAngle = (float)(Math.atan2(yDiff, -zDiff) + Math.toRadians(90));
		matrix.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(xAngle));
		
		double yzDiff = Math.sqrt(yDiff * yDiff + zDiff * zDiff);
		float zAngle = (float)Math.atan2(xDiff, yzDiff);
		matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(zAngle));
		
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, -1, 2, 0).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, -1).next();
		
		bufferBuilder.vertex(matrix, 0, 0, 0).next();
		bufferBuilder.vertex(matrix, 0, 2, 1).next();
	}

	public static void renderBlockBox(MatrixStack mStack, Vec3d bPos, float r, float g, float b, float a) {
		// Push a new item to the render stack
		mStack.push();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
        // Load the renderer
        RenderSystem.setShader(GameRenderer::getPositionShader);

        // Apply
        RenderUtils.applyRegionalRenderOffset(mStack);

        // Translate the point of rendering
        mStack.translate(
            (bPos.getX()) - RenderUtils.getRegion().getX(),
            bPos.getY(),
            (bPos.getZ()) - RenderUtils.getRegion().getZ()
        );
        
        // Update the size of the box.
        mStack.scale(1f, 1f, 1f);

		// Make it yellow
        RenderSystem.setShaderColor(r/255, g/255, b/255, a/255);
        
        // Make it so it is our mobBox.
		drawOutlinedBox(DEFAULT_BOX, mStack);

        // GL resets
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

		// Pop the stack (i.e. render it)
		mStack.pop();
	}

	public static void renderFilledBlockBox(MatrixStack mStack, Vec3d bPos, float r, float g, float b, float a) {
		// Push a new item to the render stack
		mStack.push();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		// Load the renderer
		RenderSystem.setShader(GameRenderer::getPositionShader);

		// Apply
		RenderUtils.applyRegionalRenderOffset(mStack);

		// Translate the point of rendering
		mStack.translate(
				(bPos.getX()) - RenderUtils.getRegion().getX(),
				bPos.getY(),
				(bPos.getZ()) - RenderUtils.getRegion().getZ()
		);

		// Update the size of the box.
		mStack.scale(1f, 1f, 1f);

		// Make it yellow
		RenderSystem.setShaderColor(r/255, g/255, b/255, a/255);

		// Make it so it is our mobBox.
		drawSolidBox(DEFAULT_BOX, mStack);

		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);

		// Pop the stack (i.e. render it)
		mStack.pop();
	}

	public static void renderBlockBox(MatrixStack mStack, BlockPos bPos, float r, float g, float b, float a) {
		renderBlockBox(mStack, new Vec3d(
			bPos.getX() + 0.5,
			bPos.getY(),
			bPos.getZ() + 0.5
		), r, g, b, a);
	}

	public static void renderFilledBlockBox(MatrixStack mStack, BlockPos bPos, float r, float g, float b, float a) {
		renderFilledBlockBox(mStack, new Vec3d(
				bPos.getX(),
				bPos.getY(),
				bPos.getZ()
		), r, g, b, a);
	}

	public static void renderBlockBox(MatrixStack mStack, BlockPos bPos) {
		renderBlockBox(mStack, bPos, 255, 255, 255, 255);
	}

	public static void renderBlockBox(MatrixStack mStack, BlockPos bPos, Colour colour) {
		renderBlockBox(mStack, bPos, colour.r, colour.g, colour.b, colour.a);
	}

	public static void renderBlockBox(MatrixStack mStack, Vec3d pos, Colour colour) {
		renderBlockBox(mStack, pos, colour.r, colour.g, colour.b, colour.a);
	}

	public static void fill(MatrixStack matrixStack, double x1, double y1, double x2, double y2, int color) {
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		double j;
		if (x1 < x2) {
			j = x1;
			x1 = x2;
			x2 = j;
		}

		if (y1 < y2) {
			j = y1;
			y1 = y2;
			y2 = j;
		}

		float f = (float)(color >> 24 & 255) / 255.0F;
		float g = (float)(color >> 16 & 255) / 255.0F;
		float h = (float)(color >> 8 & 255) / 255.0F;
		float k = (float)(color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.drawWithShader(bufferBuilder.end());
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
	public static void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, float x, float y, float scale, @Nullable String countLabel) {
		if (!stack.isEmpty()) {
			MatrixStack matrixStack = new MatrixStack();
			if (stack.getCount() != 1 || countLabel != null) {
				String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
				matrixStack.translate(0.0D, 0.0D, (double)(WalksyClient.getClient().getItemRenderer().zOffset + 200.0F));
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				renderer.draw(string, (float)(x + 19 - 2 - renderer.getWidth(string)), (float)(y + 6 + 3), 16777215, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 15728880);
				immediate.draw();
			}

			if (stack.isItemBarVisible()) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableBlend();
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				fill(matrixStack, x + 2, y + 13, x + 2 + 13, y + 13 + 2, 0xff000000);
				fill(matrixStack, x + 2, y + 13, x + 2 + i, y + 13 + 1, new Color(j >> 16 & 255, j >> 8 & 255, j & 255, 255).getRGB());
				RenderSystem.enableBlend();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = WalksyClient.getClient().player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (f > 0.0F) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f), 255, 255, 255, 127);
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

		}
	}

	private static void renderGuiQuad(BufferBuilder buffer, float x, float y, float width, float height, int red, int green, int blue, int alpha) {
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	public static void drawItem(ItemStack itemStack, int x, int y, double scale, boolean overlay) {
		RenderSystem.disableDepthTest();

		MatrixStack matrices = RenderSystem.getModelViewStack();

		matrices.push();
		matrices.scale((float) scale, (float) scale, 1);

		WalksyClient.getClient().getItemRenderer().renderGuiItemIcon(itemStack, (int) (x / scale), (int) (y / scale));
		if (overlay) WalksyClient.getClient().getItemRenderer().renderGuiItemOverlay(WalksyClient.getClient().textRenderer, itemStack, (int) (x / scale), (int) (y / scale), null);

		matrices.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
	}
	public static void drawItem(ItemStack stack, float xPosition, float yPosition, float scale) {
		String amountText = stack.getCount() != 1 ? stack.getCount() + "" : "";
		IItemRenderer iItemRenderer = (IItemRenderer) WalksyClient.getClient().getItemRenderer();
		iItemRenderer.renderItemIntoGUI(stack, xPosition, yPosition, scale);
		renderGuiItemOverlay(WalksyClient.getClient().textRenderer, stack, xPosition - 0.5f, yPosition + 1, scale, amountText);
	}
	public static void drawItem(ItemStack stack, float xPosition, float yPosition) {
		drawItem(stack, xPosition, yPosition, 1);
	}

	public static void drawString(String string, int x, int y, int color, float scale)
	{
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.translate(0.0D, 0.0D, 0.0D);
		matrixStack.scale(scale, scale, 1);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		WalksyClient.getClient().textRenderer.draw(string, (float) x / scale, (float) y / scale, color, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 0xF000F0);
		immediate.draw();
	}
}