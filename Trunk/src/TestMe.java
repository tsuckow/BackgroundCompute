/**
 * @(#)TestMe.java
 *
 *
 * @author 
 * @version 1.00 2006/12/21
 */

import javax.swing.*;

public class TestMe extends Test {

    public TestMe() {
    	
    	JOptionPane.showMessageDialog(null," " + BC.remoteToLocal("Title.fla","Test.fla",null));
    	JOptionPane.showMessageDialog(null,BC.LTextRB.getString("test"));
    	UseMe um = new UseMe();
    	
    	
    }
    
    
}