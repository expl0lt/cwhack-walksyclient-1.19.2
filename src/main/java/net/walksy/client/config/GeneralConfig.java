package net.walksy.client.config;

import net.walksy.client.misc.Colour;

public class GeneralConfig {
    public Colour storageColour = new Colour(255, 223, 0, 255);

    public Colour unableToPlaceCrystalColour = new Colour(255, 0, 0, 50);
    public Colour canPlaceCrystalColour = new Colour(0, 255, 0, 50);

    public Colour entityColour  = new Colour(255, 255, 255, 255);
    public String font = "walksy-client:walksy";
    public String commandPrefix = ".";
    public String alterativeCommandPrefix = ",";
    public Integer menuKey = 344;

    public GeneralConfig() {

    }
}
