package com.enation.newtest;

import java.util.*;

public class TiggerCreator {
	private List<String> seq;

	TiggerCreator(){
		seq = new ArrayList<String>();
		seq.add("S_EOP_ASK");
		seq.add("S_EOP_DATA_LOG");
		seq.add("S_EOP_REPLY");
		seq.add("S_ES_ACCESS");
		seq.add("S_ES_ADCOLUMN");
		seq.add("S_ES_ADMINTHEME");
		seq.add("S_ES_ADMINUSER");
		seq.add("S_ES_ADV");
		seq.add("S_ES_AUTH_ACTION");
		seq.add("S_ES_BORDER");
		seq.add("S_ES_FRIEND");
		seq.add("S_ES_LINK");
		seq.add("S_ES_GUESTBOOK");
		seq.add("S_ES_INDEX_ITEM");
		seq.add("S_ES_MENU");
		seq.add("S_ES_ROLE");
		seq.add("S_ES_ROLE_AUTH");
		seq.add("S_ES_SETTINGS");
		seq.add("S_ES_SHORT_MSG");
		seq.add("S_ES_SITE");
		seq.add("S_ES_SITE_MENU");
		seq.add("S_ES_THEME");
		seq.add("S_ES_THEMEURI");
		seq.add("S_ES_USER_ROLE");
		seq.add("S_ES_WIDGETBUNDLE");
	}
	
	public void printSEQ(){
		for(int i=0;i<seq.size();i++);
			//System.out.println(seq.get(i));
	}
	
	public void printTrigger(){
		String name,value;
		for(int i=0;i<seq.size();i++){
			name = seq.get(i);
			value = "CREATE TRIGGER \"TIB_" + name.substring(2) + "\" BEFORE INSERT\n" +
				"ON " + name.substring(2) + " FOR EACH ROW\n" +
				"DECLARE\n" +
				"\tINTEGRITY_ERROR  EXCEPTION;\n" +
				"\tERRNO            INTEGER;\n" +
				"\tERRMSG           CHAR(200);\n" +
				"\tDUMMY            INTEGER;\n" +
				"\tFOUND            BOOLEAN;\n" +
				"BEGIN\n" +
				"\tSELECT " + name + ".NEXTVAL INTO :NEW.ID FROM DUAL;\n" +
				"EXCEPTION\n" +
				"\tWHEN INTEGRITY_ERROR THEN\n" +
				"\t\tRAISE_APPLICATION_ERROR(ERRNO, ERRMSG);\n" +
				"END;\n" +
				"go\n";
			//System.out.println(value);
		}
	}
	
	public static void main(String[] argv){
		TiggerCreator tc = new TiggerCreator();
		tc.printTrigger();
	}
}
