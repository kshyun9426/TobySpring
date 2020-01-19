package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	
	public Integer calcSum(String filepath) throws IOException {
		
//		BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
//			@Override
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				Integer sum = 0;
//				String line = null;
//				while((line = br.readLine()) != null) {
//					sum += Integer.valueOf(line);
//				}
//				return sum;
//			}
//		};
//		return fileReadTemplate(filepath, sumCallback);
		
		//lineReadTemplate을 사용하도록 함
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value + Integer.valueOf(line);
			}
		};
		return lineReadTemplate(filepath, sumCallback, 0);
	}
	
	public Integer calcMultiply(String filepath) throws IOException {
//		BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
//			@Override
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				Integer multiply = 1;
//				String line = null;
//				while((line = br.readLine()) != null) {
//					multiply *= Integer.valueOf(line);
//				}
//				return multiply;
//			}
//		};
//		return fileReadTemplate(filepath,multiplyCallback);
		
		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line);
			}
		};
		return lineReadTemplate(filepath, multiplyCallback, 1);
	}
	
	public String concatenate(String filepath) throws IOException {
		LineCallback<String> concatenateCallback = new LineCallback<String>() {
			@Override
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		return lineReadTemplate(filepath, concatenateCallback, "");
	}
	
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			int ret = callback.doSomethingWithReader(br);
			return ret;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try {br.close();}
				catch(IOException e) {System.out.println(e.getMessage());}
			}
		}
	}
	
	
	//새로 만든 lineReadTemplate은 fileReadTemplate보다 콜백의 일반화를 더 세부적으로 다룬것이다. 
	//while루프안에서 콜백을 호출한다는점이 다른데 콜백을 여러번 반복적으로 호출하는 구조이다.
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			while((line=br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) { 
				try {br.close();}
				catch(IOException e) {System.out.println(e.getMessage());}
			}
		}
	}
	
}




















