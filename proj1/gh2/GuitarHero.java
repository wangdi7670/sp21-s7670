package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * @author: Wingd
 * @date: 2022/7/22 21:28
 *
 * 37个按键，运行程序，按下相应的按键会发声
 */
public class GuitarHero {

    static int keyNum = 37;

    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static GuitarString[] guitarStrings = new GuitarString[keyNum];

    static void init() {
        for (int i = 0; i < keyNum; i++) {
            guitarStrings[i] = new GuitarString(440 * Math.pow(2, (i - 24) / 12.0));
        }
    }

    public static void main(String[] args) {
        init();

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int i = keyboard.indexOf(key);
                if (i != -1) {
                    guitarStrings[i].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (int i = 0; i < guitarStrings.length; i++) {
                sample += guitarStrings[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < guitarStrings.length; i++) {
                guitarStrings[i].tic();
            }
        }
    }

}
