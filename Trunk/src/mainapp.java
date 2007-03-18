/**
 * @(#)mainapp.java
 *
 *
 * @author 
 * @version 1.00 2006/12/27
 */

import java.io.*;//File

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class mainapp implements Runnable
{
	

    public void run()
    {
     	Utils.iconCreate();

		Worker WorkT = new Worker();
		WorkT.start();
    }
}