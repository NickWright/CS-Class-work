/*
 *Programmer: Nick Wright
 *
 *Final Project
 *
 *Program: Given a path to a folder from the user, this program will go into the folder and each sub-folder,
 *		   cleaning the file and folder titles of any characters that aren't letters or numbers. The main program uses
 *		   a method WalkFileTree, a feature in Java. It makes it much easier to traverse all the folders 
 *		   and files in a tree, executing the code you would like on each file.
 *
 **********************************************************************************************************/

import java.util.*;
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class titleCleaner
{
	//HELPER METHODS**************************************************************************
	
	//this method cleans a file title of all the characters that aren't letters
	//returning the cleaned file title as a string
	public static String titleCleanse(String a)
	{
		String storeType = fileType(a);
		String finalTitle = "";
		String temp = "";
		
		//replace file type with an empty string
		temp = a.replace(fileType(a), "");
		
		//create a new string without the characters that aren't letters
		for (int i = 0; i < temp.length(); i++)
		{
			char b = a.charAt(i);
			
			if ((Character.isLetter(b))||(isNumeric(b)))
			{
				finalTitle += b;
			}
			else if ((!Character.isLetter(b))||(!isNumeric(b)))
			{
				finalTitle += " ";
			}
		}
		
		//concatenate the file type with the cleaned title string and shabam! Title is clean
		finalTitle = finalTitle + storeType;
		
		return finalTitle;
	}
	
	//---------------------------------------------------------------------------------------
	
	//This method indicates wether or not a file title needs to be cleaned. returns true if 
	//it does need to be cleaned, false otherwise.
	public static boolean messyFile (Path a)
	{	
		String title = a.getFileName().toString();
		String type = "";
		
		
		//replace the file type with an empty string.
		type = title.replace(fileType(title), "");
		
		
		for (int j = 0; j < type.length(); j++)
		{
			char temp = type.charAt(j);
			
			//check string for anything thats not a letter, space or a number
			if ((!(Character.isLetter(temp)))&&(!(Character.isSpace(temp)))&&(!(isNumeric(temp))))
			{
				return true;
			}
		}
		return false;
	}
	
	//---------------------------------------------------------------------------------------
	
	//this method returns the file type suffix as a string - .pdf, .gif, .bmp etc...
	public static String fileType(String a)
	{
		String storeType = "";
		
		int counter = a.length() - 1;
		
		//count backwards to store the type of file of the given string.
		while ((a.charAt(counter)!= '.')&&(counter>0))
		{
			counter--;
		}
		
		//if there is no file type, just return the empty string
		if (counter==0)
		{
			return storeType;
		}
		
		//add suffix to storeType string
		for (int i = counter; i < a.length(); i++)
		{
			storeType += a.charAt(i);
		}
		
		return storeType;
	}
	
	//-----------------------------------------------------------------------------------------
	
	//this method takes a Directory and renames the title. 
	public static void cleanFolder(Path dir)
	{
		//create a file for the directory
		File directory = new File(dir.toString());
		
        File cleanFile = new File(dir.getParent() + "\\" + 
        	titleCleanse(dir.getFileName().toString()));
        
        //rename the file
        directory.renameTo(cleanFile);  
        
        //OUTPUT****************************************
        System.out.print("Cleaning folder title: ");  		
        System.out.println(dir.getFileName().toString());
        System.out.print("New Folder Name: ");
        System.out.println(cleanFile.getName().toString());
		
	}
	
	//-----------------------------------------------------------------------------------------
	
	public static void cleanFile(Path file) throws IOException
	{
		//create the cleaned title 
  		String newTitle = titleCleanse(file.getFileName().toString());
  	
  		//replace the file with clean title in the same directory.
		Files.move(file, file.resolveSibling(newTitle));
  		
  		//OUTPUT****************************************
  		System.out.print("        Cleaning File: ");
  		System.out.println(file.getFileName().toString());
  		System.out.print("        New File Title: ");
  		System.out.println(newTitle);
  		System.out.println("");
	}
	
	//-----------------------------------------------------------------------------------------
	
	//this method just determines if a char is a number. java standard library
	//doesn't have a method for this!??
	public static boolean isNumeric(char a)
	{
		String str = Character.toString(a);
  		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	//------------------------------------------------------------------------------------------
	public static void printMenu()
	{
		System.out.println("                         FILE RUNNER v1.0                          ");
		System.out.println("               \"Cleans your file titles pretty fast\"             ");
		System.out.println("                                                                   ");
		System.out.println("Notes for use:  If the directory you are trying to clean is open   ");
		System.out.println("in file viewer it may throw some exceptions which haven't yet      ");
		System.out.println("been handled properly. Also, for ease of use enable Quick Edit in  ");
		System.out.println("the command line options to allow you to copy and paste file paths.");
		System.out.println("");
	}
	
	//MAIN PROGRAM RUNS HERE******************************************************************\\
	public static void main(String args[]) throws IOException
	{
		//get the path from the user
		String userPath = "";
		
		printMenu();
		
		while(userPath!="Q")
		{
		
			Path start = FileSystems.getDefault().getPath("");
			do
			{
				System.out.print("Please Enter a Valid Folder Path: ");
				Scanner keyboard = new Scanner(System.in);
				userPath = keyboard.nextLine();
		
				start = FileSystems.getDefault().getPath(userPath);
				String files = "";
				if (userPath.equals("Q"))
				{
					System.exit(0);
				}
			
			}
			while((! new File(userPath).exists()));
		
			System.out.println("");
			System.out.println("Start Cleanin!!!");
		
	  		//this walkfiletree method recursively goes through each folder under the root Path given
  			//and executes the title cleaning code on each file that it finds. 
  			//it cleans the folder titles as well. 
  			
  			Files.walkFileTree(start, new SimpleFileVisitor<Path>() 
  			{
  				// Visit each directory and if needed rename directory
  				@Override
   		 		public FileVisitResult preVisitDirectory(Path dir,
    	        BasicFileAttributes attrs) throws IOException
    	 	    {
       	    	 	System.out.format("Visiting Directory: %s%n", dir.getFileName().toString());
 				
 					//check if folder has messy title          
            		if (messyFile(dir))
            		{
            			//clean the title 
            			cleanFolder(dir);
            		}        	
            		System.out.println("");	
        			return FileVisitResult.CONTINUE;
    			}
  			
  				//visit each file in directory and clean it if its messy
				@Override
				public FileVisitResult visitFile(Path file,
				BasicFileAttributes attrs) throws IOException
				{
				
					//check wether current file is messy
					if (messyFile(file))
  					{
  						//clean the title	
  						cleanFile(file);					
  					}
					//continute looking for files to clean
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
}