package net.minecraft.util;

import com.studiohartman.jamepad.ControllerAxis;
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

        float deadzone = Controller.DEADZONE;
        float leftx = -Controller.getAxis(ControllerAxis.LEFTX);
        float lefty = Controller.getAxis(ControllerAxis.LEFTY);

        if (Math.abs(leftx) > deadzone) {
            this.moveStrafe = leftx;
        }

        if (Math.abs(lefty) > deadzone) {
            this.moveForward = lefty;
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

        this.jump = this.gameSettings.keyBindJump.isKeyDown() || Controller.isButtonDown(ControllerButton.A);
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() ;

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }
    }
}
