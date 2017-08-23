package ch.ethz.idsc.retina.demo.jzilly;

import ch.ethz.idsc.retina.lcm.davis.DavisLcmServer;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmViewer;

enum DavisLcmLiveView {
	;
	public static void main(String[] args) {
		DavisLcmViewer.createStandlone("jzilly", (int) 3e4);
	}
}
