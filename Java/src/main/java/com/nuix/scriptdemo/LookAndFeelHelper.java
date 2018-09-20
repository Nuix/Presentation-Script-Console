/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMER’S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMER’S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN “AS IS” AND “AS AVAILABLE” BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMER’S PURPOSES.
 */
package com.nuix.scriptdemo;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
/**
 * Helper utility to change the Java look and feel.
 * @author Jason Wells
 *
 */
public class LookAndFeelHelper {
	/**
	 * Changes the current Java look and feel to "Windows" if it is currently
	 * "Metal".
	 */
	public static void setWindowsIfMetal(){
		if(UIManager.getLookAndFeel().getName().equals("Metal")){
			try {
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if ("Windows".equals(info.getName())) {
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
