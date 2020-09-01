import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseSelector {

	/*
	 * Created global variable to keep track number of rows in file which will
	 * help in determining the size of Array later
	 */
	int rowsReturned;
	/* To read the file from the location if needed */
	String myFileName = "";
	boolean read = false;

	// This method is used for reading the file
	public int read(String filename) {
		read = true;
		rowsReturned = 0;
		myFileName = filename;

		// Code for reading the file and to handle the exceptions
		try {

			if (null == filename || filename.isEmpty()) {
				System.out.println("Filename cannot be null or empty");
				return 0;
			}

			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((br.readLine()) != null) {
				rowsReturned++;
			}

		} catch (FileNotFoundException e) {
			System.out.println("File is not found");
		} catch (NullPointerException e3) {
			System.out.println("Something Went wrong or got null data");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Returning the number of rows in the file
		return rowsReturned;

	}

	/*
	 * The recommend method accepts three parameters: courses taken by new student,
	 * minimum number of times the courses should be present and recommendations
	 */
	public ArrayList<String> recommend(String taken, int support, int recommendations) {
		/*
		 * If recommendation is zero, that means new student does not need any
		 * recommendations and no need to run the code
		 */
		if (recommendations <= 0 || support < 0 || null == taken || taken.isEmpty()) {
			return null;
		}

		/*
		 * Calling myNewExtraMethod method which will create an ArrayList of individual
		 * student courses And all the student courses data will be stored in
		 * courseTakenByStudents
		 */
		ArrayList<ArrayList<String>> courseTakenByStudents = myNewExtraMethod();

		// The courses taken by new student will be stored in coursesTakenNewStudent
		ArrayList<String> coursesTakenNewStudent = new ArrayList<String>();
		String takenArrString[] = taken.toUpperCase().split(" ");
		for (int i = 0; i < takenArrString.length; i++) {
			if (!takenArrString[i].trim().isEmpty()) {
				coursesTakenNewStudent.add(takenArrString[i]);
			}
		}

		// To find similar courses associated with the new student courses
		ArrayList<String> similarCourse = new ArrayList<String>();

		// supportCount will keep the track of how many students has such courses combination
		int supportCount = 0;
		ArrayList<String> innerList = new ArrayList<String>();
		for (int i = 0; i < courseTakenByStudents.size(); i++) {
			innerList = courseTakenByStudents.get(i);
			if (innerList.containsAll(coursesTakenNewStudent)) {
				similarCourse.addAll(innerList);
				supportCount++;
			}
		}

		/*
		 * Creating a HashMap to find the courses to recommend based on their frequency.
		 * It will loop through each courses and will check the courses in ArrayList similarCourse 
		 * contains ArrayList of courses taken by new student and will increment its value and
		 * add it to recommending  
		 */		
		LinkedHashMap<String, Integer> recommending = new LinkedHashMap<String, Integer>();
		if (supportCount >= support) {
			for (String string : similarCourse) {
				if (!coursesTakenNewStudent.contains(string)) {
					if (recommending.containsKey(string)) {
						int value = recommending.get(string);
						value++;
						recommending.put(string, value);
					} else {
						recommending.put(string, 1);
					}
				}
			}
		} else {
			return null;
		}

		/*
		 * Sorting HashMap by passing all the courses from recommending to sortValues() method
		 */		
		LinkedHashMap<String, Integer> sortedHashMap = (LinkedHashMap<String, Integer>) sortValues(recommending);

		// ArrayList created to return Recommendation
		ArrayList<String> recommendationSent = new ArrayList<String>();

		Iterator hmIterator = sortedHashMap.entrySet().iterator();
		int recommendCount = 0;
		Integer mapValue = 0;
		while (hmIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry) hmIterator.next();
			String mapKey = (String) mapElement.getKey();
			mapValue = (Integer) mapElement.getValue();
			recommendationSent.add(mapKey);
			recommendCount++;
			if (recommendCount == recommendations) {
				break;
			}
		}

		// Logic to give recommendations when there is tie between the courses
		if (sortedHashMap.containsValue(mapValue)) {
			for (Map.Entry<String, Integer> entry : sortedHashMap.entrySet()) {
				if (entry.getValue().equals(mapValue)) {
					recommendationSent.add(entry.getKey());
				}
			}
		}

		// Get unique values
		ArrayList<String> listUnique = (ArrayList<String>) recommendationSent.stream().distinct()
				.collect(Collectors.toList());
		if (listUnique.size() == 0) {
			return null;
		}
		return listUnique;
	}

	/*
	 * myNewExtraMethod method will create one temporary ArrayList i.e.
	 * indivualStudentData which will add one student courses into it at a time and
	 * at last all Students courses will be added to ArrayList of ArrayList i.e.
	 * allStudentData
	 */
	private ArrayList<ArrayList<String>> myNewExtraMethod() {
		ArrayList<ArrayList<String>> allStudentData = new ArrayList<ArrayList<String>>(rowsReturned);

		for (int i = 0; i < rowsReturned; i++) {
			/*
			 * To keep track that only particular student course data in entered into
			 * ArrayList
			 */
			int count = 0;
			String courseName = "";
			ArrayList<String> indivualStudentData = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(myFileName));
				while ((courseName = br.readLine()) != null) {
					
					String takenArrString[] = courseName.split(" ");
					if (count == i) {
						for (int j = 0; j < takenArrString.length; j++) {
							if (!takenArrString[j].trim().isEmpty()) {
								indivualStudentData.add(takenArrString[j].toUpperCase());
							}
						}
						allStudentData.add(indivualStudentData);
					}
					count++;

				}
			} catch (FileNotFoundException e) {
				System.out.println("File is not found");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return allStudentData;
	}

	// The below method will sort all the values present in the HashMap
	public static HashMap<String, Integer> sortValues(HashMap<String, Integer> hm) {
		
		// Create a list from elements of HashMap
		// https://stackoverflow.com/questions/21105413/java-java-util-map-entry-and-collection-sortlist-comparator
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				// We want sorting in descending order
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to HashMap
		// https://stackoverflow.com/questions/12184378/sorting-linkedhashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> string : list) {
			temp.put(string.getKey(), string.getValue());
		}
		return temp;
	}

	/*
	 * The below method will take courses as parameter and will generate matrix
	 * based on the courses passed as parameters and will display matrix on console
	 */
	public boolean showCommon(String coursesShow) {

		if (null == coursesShow || coursesShow.trim().isEmpty()) {
			return false;
		}

		if (!read) {
			System.out.println("Please read the file first");
			return false;
		}
		// Getting the list of all the unique courses from the students
		ArrayList<ArrayList<String>> courseTakenByStudents = myNewExtraMethod();
		ArrayList<String> courses = new ArrayList<String>();
		for (ArrayList<String> innerList : courseTakenByStudents) {
			courses.addAll(innerList);
		}
		
		// It will store the distinct courses from the ArrayList of courses
		ArrayList<String> listUnique = (ArrayList<String>) courses.stream().distinct().collect(Collectors.toList());

		try {

			String givenCourses[] = coursesShow.toUpperCase().split(" ");
			List<String> al = new ArrayList<String>();
			al = Arrays.asList(givenCourses);

			ArrayList<String> coursesShowUnique = (ArrayList<String>) al.stream().distinct()
					.collect(Collectors.toList());

			int[][] multiples = new int[coursesShowUnique.size()][coursesShowUnique.size()];
			for (int i = 0; i < coursesShowUnique.size(); i++) {
				for (int j = 0; j < coursesShowUnique.size(); j++) {
					int count = 0;
					if (i == j) {
						multiples[i][j] = 0;
					} else {
						ArrayList<String> check = new ArrayList<String>();
						check.add(coursesShowUnique.get(i));
						check.add(coursesShowUnique.get(j));
						for (ArrayList<String> innerList : courseTakenByStudents) {
							if (innerList.containsAll(check)) {
								count++;
							}
						}
						multiples[i][j] = count;
					}
				}
			}

			for (int i = 0; i < coursesShowUnique.size(); i++) {
				System.out.print(coursesShowUnique.get(i) + " ");
				for (int j = 0; j < coursesShowUnique.size(); j++) {
					System.out.print(multiples[i][j] + " ");
				}
				System.out.println();
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/*
	 * The below method will take a file as parameter and will generate matrix
	 * and write it to the file which is provided in the parameter
	 */
	public boolean showCommonAll(String filename) {

		if (!read) {
			System.out.println("Please read the file first");
			return false;
		}
		// Getting the list of all the unique courses from the students
		ArrayList<ArrayList<String>> courseTakenByStudents = myNewExtraMethod();
		ArrayList<String> courses = new ArrayList<String>();
		for (ArrayList<String> innerList : courseTakenByStudents) {
			courses.addAll(innerList);
		}
		ArrayList<String> listUnique = (ArrayList<String>) courses.stream().distinct().collect(Collectors.toList());
		Collections.sort(listUnique);

		// Checking for courses interaction from various students list
		int[][] multiples = new int[listUnique.size()][listUnique.size()];
		for (int i = 0; i < listUnique.size(); i++) {
			for (int j = 0; j < listUnique.size(); j++) {
				int count = 0;
				if (i == j) {
					multiples[i][j] = 0;
				} else {
					ArrayList<String> check = new ArrayList<String>();
					check.add(listUnique.get(i));
					check.add(listUnique.get(j));
					for (ArrayList<String> innerList : courseTakenByStudents) {
						if (innerList.containsAll(check)) {
							count++;
						}
					}
					multiples[i][j] = count;
				}
			}
		}

		try {

			if (null == filename || filename.isEmpty()) {
				System.out.println("Filename cannot be null or empty");
				return false;
			}

			// writing the matrix in the file
			FileWriter fileWriter = new FileWriter(filename);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int i = 0; i < listUnique.size(); i++) {
				printWriter.print(listUnique.get(i) + " ");
				for (int j = 0; j < listUnique.size(); j++) {
					printWriter.print(multiples[i][j] + " ");
				}
				printWriter.println();
			}

			printWriter.close();

		} catch (FileNotFoundException e) {
			System.out.println("File is not found");
			return false;
		} catch (NullPointerException e3) {
			System.out.println("Something Went wrong or got null data");
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}

		return true;
	}

}
