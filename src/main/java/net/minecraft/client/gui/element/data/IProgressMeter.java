package net.minecraft.client.gui.element.data;

public interface IProgressMeter
{
    String[] lanSearchStates = new String[] {"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

    void doneLoading();
}
