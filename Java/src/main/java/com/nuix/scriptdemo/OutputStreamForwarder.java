package com.nuix.scriptdemo;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;

/***
 * This class captures output from script container and forwards that on to an object which can consume strings.  Its
 * used by {@link com.nuix.scriptdemo.ScriptDemoDialog} to forwards output from script container to output text area.
 */
public class OutputStreamForwarder extends Writer {

	private StringBuilder buffer;
	private Consumer<String> consumer;
	private Object lock = new Object();
	
	public OutputStreamForwarder(Consumer<String> consumer){
		this.consumer = consumer;
	}
	
	@Override
	public void write(int b) {
		synchronized(lock){
			if(consumer != null){
				char c = (char) b;
		        String value = Character.toString(c);
		        buffer.append(value);
		        if (value.equals("\n")) {
		            consumer.accept(buffer.toString());
		            buffer.delete(0, buffer.length());
		        }
			}
		}
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		synchronized(lock){
			if(consumer != null){
				String value = new String(arg0);
				consumer.accept(value);
			}
		}
	}
	
	public void clearConsumer(){
		synchronized(lock){
			consumer = null;
		}
	}
}
