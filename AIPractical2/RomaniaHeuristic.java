package tour;

import search.NodeFunction;
import search.Node;
import java.util.*;

public class RomaniaHeuristic implements NodeFunction {
	
	Set<City> allCities;
	City goalCity;
	
	public int ev(Node n) {
		TourState tour = (TourState) n.state;
		City bestCity = null;
		int bestDist = 0;
		for(City city : allCities)
		{
			if(!tour.visitedCities.contains(city)) {
				if(bestCity == null || (bestDist < tour.currentCity.getShortestDistanceTo(city) + city.getShortestDistanceTo(goalCity) ) ) {
					bestCity = city;
					bestDist = tour.currentCity.getShortestDistanceTo(city) + city.getShortestDistanceTo(goalCity);
				}
			}
		}
		return bestDist;
	}
	
	public RomaniaHeuristic(Set<City> allCities, City goalCity) {
		this.allCities = allCities;
		this.goalCity = goalCity; 
	}
	
}
