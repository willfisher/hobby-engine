package com.retrochicken.engine.fx;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundClip {
	
	private Clip clip;
	private FloatControl gainControl;
	
	private float localVolume = 100;
	
	public SoundClip(String path) {
		try {
			InputStream audioSrc = getClass().getResourceAsStream(path);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														baseFormat.getSampleRate(),
														16,
														baseFormat.getChannels(),
														baseFormat.getChannels() * 2,
														baseFormat.getSampleRate(),
														false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			
			clip = AudioSystem.getClip();
			clip.open(dais);
			
			gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			setVolumeStandard(100);
			
			Settings.ACTIVE_MUSIC.add(this);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void play() {
		try {
			stop();
			
			setVolumeStandard(localVolume);
			
			clip.setFramePosition(0);
			while(!clip.isRunning())
				clip.start();
		} catch(Exception e) {}
	}
	
	public void stop() {
		try {
			if(clip.isRunning())
				clip.stop();
		} catch(Exception e) {}
	}
	
	public void close() {
		try {
			stop();
			clip.drain();
			clip.close();
		} catch(Exception e) {}
	}
	
	public void loop() {
		try {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			while(!clip.isRunning())
				clip.start();
		} catch(Exception e) {}
	}
	
	public void setVolume(float value) {
		if(gainControl == null) return;
		localVolume = 100 * value/(gainControl.getMaximum() - gainControl.getMinimum());
		gainControl.setValue(value * Settings.VOLUME/100.0f);
	}
	
	public void setVolumeStandard(float value) {
		if(gainControl == null) return;
		if(value < 0 || value > 100)
			return;
		
		localVolume = value;
		
		float newVol = gainControl.getMinimum() + ((gainControl.getMaximum() - gainControl.getMinimum()) * value/100.0f) * Settings.VOLUME/100.0f;
		if(newVol > gainControl.getMaximum())
			newVol = gainControl.getMaximum();
		else if(newVol < gainControl.getMinimum())
			newVol = gainControl.getMinimum();
		
		gainControl.setValue(newVol);
	}
	
	public void adjustVolume() {
		setVolumeStandard(localVolume);
	}
	
	public boolean isRunning() {
		return clip.isRunning();
	}
}
