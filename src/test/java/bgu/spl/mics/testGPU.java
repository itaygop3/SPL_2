package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Stack;
import org.junit.jupiter.api.Test;

class testGPU {

	@Test
	public void testSendData() {
		GPU gpu = new GPU(GPU.Type.RTX3090, new Cluster(), "", new GPUService("name"));
		Stack<DataBatch> s = gpu.getBatches();
		int preIndex = s.peek().getIndex();
		gpu.sendData();
		int postIndex = s.peek().getIndex();
		assertEquals(preIndex-1000, postIndex);
	}
	
	@Test
	public void testReciveProcessedData() {
		GPU gpu = new GPU(GPU.Type.RTX3090, new Cluster(), "", new GPUService("name"));
		assertFalse(gpu.isFull());
		DataBatch db = new DataBatch(new Data(Data.Type.Tabular, 1000*20), 0);
		int preSize = gpu.getVRAMSize();
		int preNext = gpu.getNext();
		gpu.reciveProcessedData(db);
		int postNext = gpu.getNext();
		int postSize = gpu.getVRAMSize();
		assertEquals(preSize+1, postSize);
		assertEquals((preNext+1)%gpu.getVRAMCapacity(),postNext);
	}
	
	@Test
	public void testTrainProcessedData() {
		GPU gpu = new GPU(GPU.Type.RTX3090, new Cluster(), "", new GPUService("name"));
		DataBatch db = new DataBatch(new Data(Data.Type.Tabular, 1000*20), 0);
		gpu.reciveProcessedData(db);
		int preSize = gpu.getVRAMSize();
		int preProcessed = db.getData().getProcessed();
		DataBatch trainedDB = gpu.trainProcessedData();
		int postSize = gpu.getVRAMSize();
		int postProcessed  = trainedDB.getData().getProcessed();
		assertEquals(preSize-1, postSize);
		assertEquals(preProcessed+1000, postProcessed);
		assertFalse(gpu.isFull());
	}

}
