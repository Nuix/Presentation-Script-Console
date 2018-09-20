script_directory = File.dirname(__FILE__)
require File.join(script_directory,"PresentationScriptConsole.jar")
java_import com.nuix.scriptdemo.ScriptDemoDialog
java_import com.nuix.scriptdemo.LookAndFeelHelper

LookAndFeelHelper.setWindowsIfMetal
dialog = ScriptDemoDialog.new($utilities,$current_case,$current_selected_items,$window)
dialog.setDefaultScriptLocation(File.join(script_directory,"Scripts"))
Dir.glob("#{script_directory}/Snippets/*.rb").sort.each do |snippet_file|
	dialog.addSnippetToMenu(File.basename(snippet_file,".rb"),snippet_file)
end

dialog.display