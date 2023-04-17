package com.konnect.jpms.util;

/**
 * Collection of useful utilities to work with arrays. 
 * 
 * @version $Id: ArrayUtils.java
 * @author Konnect
 */
public final class ArrayUtils
{
      
   /**
    * Test if specified array contains given element and if it does, find 
    * its position.
    * 
    * @param source - array to search, can be null
    * @return int - -1 if it doesn't exist there otherwise its position
    */
   public static int contains(
      String[] source,
      String   sTarget
   )
   {
	   
	   int iTarget = Integer.parseInt(sTarget);
      int iReturn = -1;
      
      if ((source != null) && (source.length > 0))
      {   
         int iIndex;
//         System.out.println("iTarget ===> " + iTarget);
         for (iIndex = 0; iIndex < source.length; iIndex++)
         {
//        	 System.out.println("source[iIndex] ===> " + source[iIndex]);
            if (source[iIndex] != null && !source[iIndex].equals("") && Integer.parseInt(source[iIndex]) == iTarget)
            {
               iReturn = iIndex;
               break;
            }
         }
      } 
//      System.out.println("iReturn ===> " + iReturn);
      return iReturn;
   }

   
   /** 
   * @param source - array to search, can be null
   * @return int - -1 if it doesn't exist there otherwise its position
   */
  public static int containsString(
     String[] source,
     String   sTarget
  )
  {
	   
     int iReturn = -1;
     
     if ((source != null) && (source.length > 0))
     {   
        int iIndex;
        
        for (iIndex = 0; iIndex < source.length; iIndex++)
        {
           if (source[iIndex]!=null && source[iIndex].equalsIgnoreCase(sTarget))
           {
              iReturn = iIndex;
              break;
           }
        }
     }
     
     return iReturn;
  }
   
   /**
    * Sum all elements in the array.
    * 
    * @param source - array to sum elements of
    * @return long - sum of the elements in the array
    */
   public static long sum(
      int[] source
   )
   {
      int iReturn = 0;
      
      if ((source != null) && (source.length > 0))
      {   
         int iIndex;
         
         for (iIndex = 0; iIndex < source.length; iIndex++)
         {
            iReturn += source[iIndex];
         }
      }
      
      return iReturn;
   }
}
