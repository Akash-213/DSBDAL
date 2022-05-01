package LogFile1;


import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class UserCount {
	
public static void main(String [] args) throws Exception {

	Configuration c=new Configuration();
	String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
	Path input=new Path(files[0]);
	Path output=new Path(files[1]);
	Job j=new Job(c,"wordcount");
	j.setJarByClass(UserCount.class);
	j.setMapperClass(MapForUserCount.class);
	j.setReducerClass(ReduceForUserCount.class);
	j.setOutputKeyClass(Text.class);
	j.setOutputValueClass(IntWritable.class);
	FileInputFormat.addInputPath(j, input);
	FileOutputFormat.setOutputPath(j, output);
	System.exit(j.waitForCompletion(true)?0:1);
}


public static class MapForUserCount extends Mapper<LongWritable, Text, Text, IntWritable>{
	
    private final static IntWritable cntOne = new IntWritable(1);
    private Text word = new Text();
	
	public void map(LongWritable key, Text value, Context con) throws IOException, InterruptedException {
		
		StringTokenizer itr = new StringTokenizer(value.toString(),",");
		
		while(itr.hasMoreTokens()){
			
			// skipping first 2 entries
			itr.nextToken();
			itr.nextToken();
			
			// set the ip in word variable
			word.set(itr.nextToken());
			
			// tokenize via '.'
			StringTokenizer itr2 = new StringTokenizer(word.toString(),".");
			
			// validate by checking 1.0.1.0 characters as 4
	    	int count = 0;
	    	while(itr2.hasMoreTokens()){
	    		  count++;
	    		  itr2.nextToken();
	    	}
	    	if(count==4){
	    		con.write(word,cntOne);
	    	}
	    	itr.nextToken();
	    	itr.nextToken();
	    	itr.nextToken();
	    	itr.nextToken();
	    	itr.nextToken();
			
			
		}
	}
}

public static class ReduceForUserCount extends Reducer<Text, IntWritable, Text, IntWritable>{
	
    private IntWritable result = new IntWritable();
     
	public void reduce(Text key, Iterable<IntWritable> values, Context con) throws IOException, InterruptedException{
		
		int sum = 0;
		
		for(IntWritable val : values){
			sum += val.get();
		}
		result.set(sum);
		
		con.write(key, result);
		
	}
}

}