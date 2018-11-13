package com.sohlman.restbench;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sohlman.restbench.model.Information;
import com.sohlman.restbench.model.impl.InformationImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestBench extends AbstractVerticle {
	@Override
	public void start() {

		hostName = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (UnknownHostException ex) {
			System.err.println("Hostname can not be resolved");
		}

		loadDefaultData();

		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());
		router.get("/informations/random/:count").handler(this::getRandomData);
		router.get("/informations/random/:count/:waitTime").handler(this::getRandomData);
		
		router.get("/informations/count").handler(this::getCount);
		router.get("/informations/:count/:start").handler(this::getList);
		router.get("/informations/:count").handler(this::getList);
		router.get("/informations").handler(this::getList);
		router.post("/information/:index").handler(this::add);
		router.post("/information").handler(this::add);
		router.get("/information/:index").handler(this::get);
		router.put("/information/:index").handler(this::set);
		router.get("/maxTimeout").handler(this::getMaxTimeOut);
		router.put("/maxTimeout/:timeout").handler(this::setMaxTimeout);
		
		router.delete("/information/:index").handler(this::delete);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	protected void loadDefaultData() {
		this.list = Collections.synchronizedList(new ArrayList<>());
		list.add(new InformationImpl("Kokko", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("House", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Word", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("What", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Häh", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Voitto", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Sana", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Kana", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Plane", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Churro", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Coche", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Multa", Util.randomNumber(1, 100000)));
		list.add(new InformationImpl("Confusing", Util.randomNumber(1, 100000)));
	}

	protected Information getRandomInformation() {
		return list.get(Util.randomNumber(0, list.size() - 1));
	}
	
	private void getCount(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			String startStr = routingContext.request().getParam("start");
			String countStr = routingContext.request().getParam("count");
			
			int start = Util.getIntFromString(startStr, 0, list.size() - 1, 0);
			int end = Util.getIntFromString(countStr, list.size() - 1, list.size() - 1);

			HttpServerResponse response = routingContext.response();
			
			response.putHeader("content-type", "application/json; charset=utf-8").end(String.valueOf(list.size()));
		});
	}

	private void getList(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			String startStr = routingContext.request().getParam("start");
			String countStr = routingContext.request().getParam("count");
			
			int start = Util.getIntFromString(startStr, 0, list.size() - 1, 0);
			int end = Util.getIntFromString(countStr, list.size() - 1, list.size() - 1);

			HttpServerResponse response = routingContext.response();
			
			if (list.isEmpty()) {
				response.putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(list));
				
			}
			else {
				response.putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(list.subList(start,end)));	
			}
		});			
	}

	private void add(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			final Information information = Json.decodeValue(routingContext.getBodyAsString(), InformationImpl.class);
			
			int index = Util.getIntFromString(routingContext.request().getParam("index"), 0, list.size());
			
			this.list.add(index, information);

			routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(information));
		});			
	}

	private void set(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			final Information information = Json.decodeValue(routingContext.getBodyAsString(), InformationImpl.class);

			
			if (information==null) {
				routingContext.response().setStatusCode(400).end("No value received");			
			}
			
			int index = -1;
			try {
				index = Integer.valueOf(routingContext.request().getParam("index"));
			} catch (NumberFormatException nfe) {
			}
			
			if (index >= 0 && index < list.size()) {
				this.list.set(index, information);
				routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(information));
			}
			else {
				routingContext.response().setStatusCode(204).end("No content ant index " + index);
			}
		});		
	}	
	
	private void delete(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			int index = -1;
			String indexValue = routingContext.request().getParam("index");
			
			if (indexValue==null) {
				routingContext.response().setStatusCode(404).end();
			}
	 		
			try {
				index = Integer.valueOf(indexValue);
			} catch (NumberFormatException nfe) {
				routingContext.response().setStatusCode(400).end(nfe.getMessage());
				return;
			}
			if (index >= 0 && index < list.size()) {
				Information information = list.remove(index);
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(information));
			}
			else {
				routingContext.response().setStatusCode(404).end();
			}
		});			
	}

	private void get(RoutingContext routingContext) {
		vertx.setTimer(Util.randomNumber(0, this.maxTimeOut), id -> {
			int index = -1;
			try {
				index = Integer.valueOf(routingContext.request().getParam("index"));
			} catch (NumberFormatException nfe) {
				routingContext.response().setStatusCode(400).end(nfe.getMessage());
				return;
			}
			if (index >= 0 && index < list.size()) {
				Information information = this.list.get(index);
				
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(information));
			}
			else {
				routingContext.response().setStatusCode(404).end();
			}
		});
	}
	
	private void getRandomData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();

		int waitTime = Util.getIntFromString(routingContext.request().getParam("waitTime"), 0, this.maxTimeOut, Util.randomNumber(0, this.maxTimeOut));
		int count = Util.getIntFromString(routingContext.request().getParam("count"),1,100,1);
		
		if (waitTime==0) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.put("hostName", hostName);
			jsonObject.put("waitTime", waitTime);
			
			List<Information> randomList = new ArrayList<>(count);
			for ( int i=0 ; i < count ; i++ ) {
				randomList.add(list.get(Util.randomNumber(0, list.size() - 1)));
			}
			
			jsonObject.put("data", randomList);
			response.putHeader("content-type", "application/json; charset=utf-8").end(jsonObject.encodePrettily());			
		}
		else {
			vertx.setTimer(waitTime, id -> {
				JsonObject jsonObject = new JsonObject();
				jsonObject.put("hostName", hostName);
				jsonObject.put("waitTime", waitTime);
				
				List<Information> randomList = new ArrayList<>(count);
				
				for ( int i=0 ; i < count ; i++ ) {
					randomList.add(list.get(Util.randomNumber(0, list.size() - 1)));
				}
				
				jsonObject.put("data", randomList);
				response.putHeader("content-type", "application/json; charset=utf-8").end(jsonObject.encodePrettily());
			});
		}

	}

	private void getMaxTimeOut(RoutingContext routingContext) {
		routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
		.end(Json.encodePrettily(Long.valueOf(this.maxTimeOut)));
	}
	
	private void setMaxTimeout(RoutingContext routingContext) {
		try {
			int timeout = Integer.valueOf(routingContext.request().getParam("timeout"));
			this.maxTimeOut = timeout;
			routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
			.end(Json.encodePrettily(this.maxTimeOut));
		} catch (NumberFormatException nfe) {
			routingContext.response().setStatusCode(204).end("Error " + nfe.getMessage());
		}
	}
	
	private String hostName = "unknown";
	
	private int maxTimeOut = 1000;
	
	private List<Information> list;
}