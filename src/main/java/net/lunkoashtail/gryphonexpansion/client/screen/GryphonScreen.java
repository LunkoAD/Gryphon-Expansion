package net.lunkoashtail.gryphonexpansion.client.screen;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.lunkoashtail.gryphonexpansion.menu.GryphonMenu;
import net.lunkoashtail.gryphonexpansion.network.SetGryphonNamePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * GryphonScreen — redesigned clean layout (320 × 266 px).
 *
 * <pre>
 * ┌──────────────────────────────────────────────────────────┐
 * │  [Name field_____________]   ♂ Male · Lv 3 · Age 3d     │  y=7
 * ├──────────────────────────────────────────────────────────┤  y=26
 * │                     │  "Saddle"  ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * │  3D Preview (90×95) │  [saddle]  ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * │                     │  "Armor"   ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * │                     │  [armor]   ║ ■    Storage    ■    │
 * │                     │  "Harness" ║ ■    (8 × 8)    ■    │
 * │                     │  [harness] ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * ├─────────────────────┤            ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * │ HP   [██████░░] 32  │            ║ ■ ■ ■ ■ ■ ■ ■ ■    │
 * │ Hun  [██████░░] 80% │            ╚═══════════════════   │
 * │ Trs  [████░░░░] 45% │                                   │
 * │ Spd 5.6  Dmg 7.3   │                                   │
 * ├──────────────────────────────────────────────────────────┤  y=182
 * │              [Player Inventory  9 × 3]                   │
 * │              [Hotbar            9 × 1]                   │
 * └──────────────────────────────────────────────────────────┘  y=266
 * </pre>
 *
 * <p><b>3D preview:</b> drag left-click to rotate via accumulated mouse offsets
 * ({@link #previewMouseOffX}, {@link #previewMouseOffY}) fed to
 * {@link InventoryScreen#renderEntityInInventoryFollowsMouse}.
 *
 * <p><b>No GUI texture required</b> — all backgrounds are drawn with
 * {@code GuiGraphics.fill()}. See bottom of file for texture notes.
 */
public class GryphonScreen extends AbstractContainerScreen<GryphonMenu> {

    // ─── Texture ──────────────────────────────────────────────────────────────────

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            GryphonExpansion.MOD_ID, "textures/gui/gryphon_gui.png");

    // ─── Dimensions ───────────────────────────────────────────────────────────────

    public static final int IMAGE_WIDTH  = 320;
    public static final int IMAGE_HEIGHT = 280;

    // ─── Preview ──────────────────────────────────────────────────────────────────

    private static final int PREVIEW_X     = 8;
    private static final int PREVIEW_Y     = 28;
    private static final int PREVIEW_W     = 90;
    private static final int PREVIEW_H     = 95;
    private static final int PREVIEW_SCALE = 38;

    // ─── Name field ───────────────────────────────────────────────────────────────

    private static final int NAME_X = 8;
    private static final int NAME_Y = 8;
    private static final int NAME_W = 172;
    private static final int NAME_H = 14;

    // ─── Stat bars (compact, inline label + bar + value on one row) ───────────────

    private static final int STAT_X       = 8;
    private static final int STAT_Y       = 134;  // below gear slot 4 bottom (y=128+6)
    private static final int STAT_LABEL_W = 26;   // pixels reserved for label text
    private static final int STAT_BAR_W   = 90;   // bar fill width
    private static final int STAT_ROW_H   = 14;   // gap between row tops

    // ─── Palette ──────────────────────────────────────────────────────────────────

    private static final int C_BAR_BG     = 0xFF111111;
    private static final int C_HP         = 0xFFD94040;
    private static final int C_HUNGER     = 0xFFE8933A;
    private static final int C_TRUST      = 0xFF3A7FD4;
    private static final int C_TEXT       = 0xFFDDDDDD;
    private static final int C_DIM        = 0xFF888888;
    private static final int C_MALE       = 0xFF6AABFF;
    private static final int C_FEMALE     = 0xFFFF8ABF;

    // ─── State ────────────────────────────────────────────────────────────────────

    private final GryphonEntity gryphon;

    // Fake mouse position offsets driving the preview rotation.
    // The vanilla helper rotates the entity toward the given mouse coords;
    // we accumulate drag deltas here so drag = rotation.
    private float previewMouseOffX = 28.0f;
    private float previewMouseOffY = 8.0f;

    private EditBox nameField;

    // ─── Constructor ──────────────────────────────────────────────────────────────

    public GryphonScreen(GryphonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.gryphon      = menu.gryphon;
        this.imageWidth   = IMAGE_WIDTH;
        this.imageHeight  = IMAGE_HEIGHT;
        // Suppress vanilla title/inventory labels — we draw our own
        this.titleLabelX      = -10000;
        this.inventoryLabelX  = -10000;
    }

    // ─── Init ─────────────────────────────────────────────────────────────────────

    @Override
    protected void init() {
        super.init();

        String existingName = (gryphon != null && gryphon.hasCustomName())
                ? gryphon.getCustomName().getString() : "";

        nameField = new EditBox(this.font,
                leftPos + NAME_X, topPos + NAME_Y,
                NAME_W, NAME_H,
                Component.literal("Name…"));
        nameField.setMaxLength(64);
        nameField.setValue(existingName);
        nameField.setTextColor(0xFFFFFFFF);
        nameField.setBordered(true);
        nameField.setResponder(name -> {
            if (gryphon != null)
                PacketDistributor.sendToServer(new SetGryphonNamePacket(gryphon.getId(), name));
        });

        addRenderableWidget(nameField);
    }

    // ─── Top-level render ─────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    // ─── Background ───────────────────────────────────────────────────────────────

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        int lx = leftPos;
        int ty = topPos;

        // ── Background texture ────────────────────────────────────────────────────
        // Blits the full 320×280 GUI texture; all panels, borders, slot insets,
        // dividers, and the header separator are painted on the texture itself.
        // Dynamic elements (stat bars, locked overlay, entity preview) are still
        // drawn on top programmatically.
        g.blit(TEXTURE, lx, ty, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);

        // ── Header row ────────────────────────────────────────────────────────────

        // Info badge to the right of name field: gender · level · age
        if (gryphon != null) {
            boolean male = gryphon.isGenderMale();
            String badge = String.format("%s %s  ·  Lv %d  ·  Age %dd",
                    male ? "♂" : "♀",
                    male ? "Male" : "Female",
                    gryphon.getLevel(),
                    gryphon.getAgeInDays());
            int badgeColor = male ? C_MALE : C_FEMALE;
            g.drawString(font, badge, lx + NAME_X + NAME_W + 8, ty + NAME_Y + 2, badgeColor, false);
        }

        // ── 3D preview ────────────────────────────────────────────────────────────
        int px = lx + PREVIEW_X;
        int py = ty + PREVIEW_Y;

        if (gryphon != null) {
            int cx = px + PREVIEW_W / 2;
            int cy = py + PREVIEW_H / 2;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    g, px, py, px + PREVIEW_W, py + PREVIEW_H,
                    PREVIEW_SCALE, 0.0f,
                    cx + previewMouseOffX, cy + previewMouseOffY,
                    gryphon);
        }

        // ── Gear slot labels ──────────────────────────────────────────────────────
        renderGearSlotLabel(g, lx, ty, GryphonMenu.GEAR_SLOT_0_Y, "Saddle");
        renderGearSlotLabel(g, lx, ty, GryphonMenu.GEAR_SLOT_1_Y, "Armor");
        renderGearSlotLabel(g, lx, ty, GryphonMenu.GEAR_SLOT_2_Y, "Harness");
        renderGearSlotLabel(g, lx, ty, GryphonMenu.GEAR_SLOT_3_Y, "Chest");

        // ── Compact stat bars ─────────────────────────────────────────────────────
        if (gryphon != null) {
            renderStats(g, lx, ty);
        }

        // ── Locked overlay (drawn on top of storage grid when no chest equipped) ──
        boolean chestEquipped = gryphon != null && gryphon.hasChestEquipped();
        if (!chestEquipped) {
            int gx  = lx + GryphonMenu.INV_START_X - 1;
            int gy  = ty + GryphonMenu.INV_START_Y - 1;
            int gw  = 8 * 18 + 2;
            int gh  = 8 * 18 + 2;
            int midX = gx + gw / 2;
            int midY = gy + gh / 2;
            g.fill(gx, gy, gx + gw, gy + gh, 0xCC0A0A0A);
            g.drawCenteredString(font, "No Chest equipped",          midX, midY - 10, 0xFF666666);
            g.drawCenteredString(font, "Add a Chest to the gear slot", midX, midY + 2,  0xFF444444);
        }
    }

    // ─── Gear slot helper ─────────────────────────────────────────────────────────

    private void renderGearSlotLabel(GuiGraphics g, int lx, int ty, int slotY, String label) {
        int sx = lx + GryphonMenu.GEAR_SLOT_X;
        int sy = ty + slotY;
        g.drawString(font, label, sx, sy - 9, C_DIM, false);
    }

    // ─── Compact stat rows ────────────────────────────────────────────────────────

    private void renderStats(GuiGraphics g, int lx, int ty) {
        int bx = lx + STAT_X;

        // HP
        int y0 = ty + STAT_Y;
        renderStatRow(g, bx, y0, "HP",
                String.format("%.0f/%.0f", gryphon.getHealth(), gryphon.getMaxHealth()),
                gryphon.getHealth() / gryphon.getMaxHealth(), C_HP);

        // Hunger
        int y1 = ty + STAT_Y + STAT_ROW_H;
        renderStatRow(g, bx, y1, "Hun",
                String.format("%.0f%%", gryphon.getHunger()),
                gryphon.getHunger() / GryphonEntity.HUNGER_MAX, C_HUNGER);

        // Trust
        int y2 = ty + STAT_Y + STAT_ROW_H * 2;
        renderStatRow(g, bx, y2, "Trs",
                String.format("%.0f%%", gryphon.getTrust()),
                gryphon.getTrust() / GryphonEntity.TRUST_MAX, C_TRUST);

        // Scalar values on a single compact line
        int y3 = ty + STAT_Y + STAT_ROW_H * 3 + 4;
        g.drawString(font,
                String.format("Spd %.1f  Dmg %.1f  Arm %.0f",
                        gryphon.getSpeedForDisplay(), gryphon.getDamageForDisplay(),
                        gryphon.getArmorForDisplay()),
                bx, y3, C_DIM, false);
    }

    /**
     * Draws one stat row: "LABEL [bar░░░░] VALUE" all on the same baseline.
     *
     * @param x    left edge of the label
     * @param y    top of the row (bar is vertically centred within the font line)
     * @param pct  fill fraction 0–1
     */
    private void renderStatRow(GuiGraphics g, int x, int y, String label, String value, float pct, int color) {
        // Label (left-aligned, dim)
        g.drawString(font, label, x, y, C_DIM, false);

        // Bar (sits 1px below the font baseline to feel centred)
        int barX = x + STAT_LABEL_W;
        int barY = y + 1;
        int barH = 6;
        g.fill(barX, barY, barX + STAT_BAR_W, barY + barH, C_BAR_BG);
        int filled = (int)(STAT_BAR_W * Mth.clamp(pct, 0f, 1f));
        if (filled > 0) g.fill(barX, barY, barX + filled, barY + barH, color);
        // Subtle top highlight
        g.fill(barX, barY, barX + STAT_BAR_W, barY + 1, 0x18FFFFFF);

        // Value (right of bar)
        g.drawString(font, value, barX + STAT_BAR_W + 4, y, C_TEXT, false);
    }

    // ─── Mouse drag — preview rotation ────────────────────────────────────────────

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dX, double dY) {
        if (button == 0 && isOverPreview(mouseX, mouseY)) {
            previewMouseOffX += (float) dX;
            previewMouseOffY += (float) dY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dX, dY);
    }

    private boolean isOverPreview(double mx, double my) {
        int px = leftPos + PREVIEW_X;
        int py = topPos  + PREVIEW_Y;
        return mx >= px && mx < px + PREVIEW_W && my >= py && my < py + PREVIEW_H;
    }

}

// ─── Texture notes ────────────────────────────────────────────────────────────
//
// GUI textures (none required — everything is drawn programmatically):
//   If you later want a hand-painted background like vanilla chest/horse screens,
//   add a 320×266 PNG at:
//     src/main/resources/assets/gryphonexpansion/textures/gui/gryphon_gui.png
//   Then replace the g.fill() calls in renderBg with a single:
//     g.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
//
// Entity texture (REQUIRED — game crashes without it):
//   src/main/resources/assets/gryphonexpansion/textures/entity/gryphon/gryphon.png
//   Size: 128 × 64 px  (standard mob sheet; increase if adding more detail)
//   Any placeholder PNG of that size works — the 3D preview will paint the model
//   with whatever image you put there.
