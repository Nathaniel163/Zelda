import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

class Sound extends Thread {
Clip clip;
String soundS;
Sound(String sound) {
soundS = sound;
File soundF = new File("sounds\\" + sound + ".wav");// file for music
PlaySound(soundF);
}

void PlaySound(File Sound) {
try {
clip = AudioSystem.getClip();
clip.open(AudioSystem.getAudioInputStream(Sound));
if (soundS == "battlemusic") {
clip.loop(10);
}
else {
clip.start();
}
} catch (Exception e) {
}
}

void cancel() {
clip.close();
}
}