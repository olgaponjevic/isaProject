package com.example.isaProtobufClient;

import com.example.isaProtobufClient.proto.MessageProto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class IsaProtobufClientApplication implements CommandLineRunner {

	private static final String SERVER_URL = "http://localhost:8090/api";
	private static final int NUMBER_OF_MESSAGES = 50;

	public static void main(String[] args) {
		SpringApplication.run(IsaProtobufClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n=== POREĐENJE JSON vs PROTOBUF ===\n");

		Thread.sleep(1000);

		BenchmarkResult jsonResult = runJsonBenchmark();
		BenchmarkResult protoResult = runProtobufBenchmark();

		printResults(jsonResult, protoResult);
	}

	private BenchmarkResult runJsonBenchmark() throws Exception {
		System.out.println(">>> Testiranje JSON formata...");

		HttpClient client = HttpClient.newHttpClient();
		ObjectMapper mapper = new ObjectMapper();

		long totalBytesSent = 0;
		long totalBytesReceived = 0;

		long startTime = System.currentTimeMillis();

		for (int i = 1; i <= NUMBER_OF_MESSAGES; i++) {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("id", i);
			requestMap.put("content", "Poruka broj " + i);
			requestMap.put("timestamp", System.currentTimeMillis());

			byte[] requestBytes = mapper.writeValueAsBytes(requestMap);
			totalBytesSent += requestBytes.length;

			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(SERVER_URL + "/json"))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes))
					.build();

			HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
			totalBytesReceived += response.body().length;
		}

		long elapsed = System.currentTimeMillis() - startTime;

		return new BenchmarkResult("JSON", elapsed, totalBytesSent, totalBytesReceived);
	}

	private BenchmarkResult runProtobufBenchmark() throws Exception {
		System.out.println(">>> Testiranje Protobuf formata...");

		HttpClient client = HttpClient.newHttpClient();

		long totalBytesSent = 0;
		long totalBytesReceived = 0;

		long startTime = System.currentTimeMillis();

		for (int i = 1; i <= NUMBER_OF_MESSAGES; i++) {
			MessageProto.Request request = MessageProto.Request.newBuilder()
					.setId(i)
					.setContent("Poruka broj " + i)
					.setTimestamp(System.currentTimeMillis())
					.build();

			byte[] requestBytes = request.toByteArray();
			totalBytesSent += requestBytes.length;

			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(SERVER_URL + "/protobuf"))
					.header("Content-Type", "application/x-protobuf")
					.POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes))
					.build();

			HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
			totalBytesReceived += response.body().length;
		}

		long elapsed = System.currentTimeMillis() - startTime;

		return new BenchmarkResult("Protobuf", elapsed, totalBytesSent, totalBytesReceived);
	}

	private void printResults(BenchmarkResult json, BenchmarkResult proto) {
		System.out.println("\n========================================");
		System.out.println("REZULTATI BENCHMARK-a (" + NUMBER_OF_MESSAGES + " poruka)");
		System.out.println("========================================\n");

		System.out.printf("%-20s | %-15s | %-20s | %-20s%n",
				"Format", "Vreme (ms)", "Poslato (B)", "Primljeno (B)");
		System.out.println("-".repeat(80));

		System.out.printf("%-20s | %-15d | %-20d | %-20d%n",
				json.format, json.timeMs, json.bytesSent, json.bytesReceived);

		System.out.printf("%-20s | %-15d | %-20d | %-20d%n",
				proto.format, proto.timeMs, proto.bytesSent, proto.bytesReceived);

		System.out.println("\n>>> ANALIZA:");

		double timeRatio = (double) json.timeMs / proto.timeMs;
		System.out.printf("- Protobuf je %.2fx %s od JSON-a po vremenu%n",
				timeRatio > 1 ? timeRatio : 1.0 / timeRatio,
				timeRatio > 1 ? "brži" : "sporiji");

		double sizeSentRatio = (double) json.bytesSent / proto.bytesSent;
		System.out.printf("- Protobuf zahtevi su %.2fx %s od JSON zahteva%n",
				sizeSentRatio > 1 ? sizeSentRatio : 1.0 / sizeSentRatio,
				sizeSentRatio > 1 ? "manji" : "veći");

		double sizeReceivedRatio = (double) json.bytesReceived / proto.bytesReceived;
		System.out.printf("- Protobuf odgovori su %.2fx %s od JSON odgovora%n",
				sizeReceivedRatio > 1 ? sizeReceivedRatio : 1.0 / sizeReceivedRatio,
				sizeReceivedRatio > 1 ? "manji" : "veći");

		System.out.println("\n========================================\n");
	}

	private static class BenchmarkResult {
		String format;
		long timeMs;
		long bytesSent;
		long bytesReceived;

		BenchmarkResult(String format, long timeMs, long bytesSent, long bytesReceived) {
			this.format = format;
			this.timeMs = timeMs;
			this.bytesSent = bytesSent;
			this.bytesReceived = bytesReceived;
		}
	}
}