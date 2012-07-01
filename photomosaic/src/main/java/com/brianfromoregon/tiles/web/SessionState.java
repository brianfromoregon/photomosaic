package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ColorSpace;
import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.ProcessedIndex;

import java.awt.image.BufferedImage;

/**
 * This is bad, need to ask a web dev how to store
 * 1. user preferences
 * 2. input from previous steps in a workflow
 */
public class SessionState {
    public static Index palette;
    public static BufferedImage target;
}
