package net.minecraft.util;

import com.studiohartman.jamepad.ControllerButton;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Controller;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        float deadzone = Controller.STICK_DEADZONE;
        float moveX = -Controller.getAxis(0);
        float moveY = Controller.getAxis(1);

        if (Math.abs(moveX) > deadzone) {
            this.moveStrafe = moveX;
        }

        if (Math.abs(moveY) > deadzone) {
            this.moveForward = moveY;
        }

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown() || Controller.isButtonDown(ControllerButton.A.ordinal());
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() ;

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }
    }
}
