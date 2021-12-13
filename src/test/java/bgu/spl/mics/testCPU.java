package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Queue;
import org.junit.jupiter.api.Test;



class testCPU {

	@Test
	void testSendProcessedData() {
		CPU cpu = new CPU(4, new Cluster());
		DataBatch db = new DataBatch(new Data(Data.Type.Images, 1000), 0);
		cpu.insertData(db);
		cpu.sendProcessedData();
		assertNotSame(db,cpu.getData().peek());
		
	}

}
