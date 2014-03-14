================================================
XML CONFIG FILE
================================================

- the root element is &lt;simulation&gt;.
	- &lt;simulation&gt; contains 1 or more &lt;case&gt;
		- &lt;case&gt; contains 1 &lt;runs&gt;, 1 &lt;inf&gt;, 1 or more &lt;strategy&gt;, 1 or more &lt;innovator&gt;, 0 or more &lt;provider&gt; 
			- &lt;runs&gt; contains an integer, which indicates the number of runs of the case.
			- &lt;inf&gt; contains a string, which MUST BE an ABSOLUTE path to the influence matrix txt file OR an RELATIVE path to the jar files.
			- &lt;strategy&gt; contains a string, which could be "closed", "licensing", "outsourcing", "alliance_max", "alliance_min".
			- &lt;innovator&gt; contains 1 &lt;num&gt;, 1 &lt;power&gt;, 1 &lt;M&gt; and 1 &lt;P&gt;
				- &lt;num&gt; contains an integer, which indicates the total number of that type of agents with within that case
				- &lt;power&gt; contains an integer, which indicates the processing power of that type of agent
				- &lt;M&gt; contains an integer, which indicates the size of M
				- &lt;P&gt; contains an integer, which indicates the size of P
			- &lt;provider&gt; contains 1 &lt;num&gt;, 1 &lt;power&gt;, 1 &lt;Q&gt;
				- &lt;num&gt; contains an integer, which indicates the total number of that type of agents with within that case
				- &lt;power&gt; contains an integer, which indicates the processing power of that type of agent
				- &lt;Q&gt; contains an integer, which indicates the size of Q

================================================
RD_run
================================================
RD_run takes only one argument which is the path (absolute or relative path) to an xml config file.

E.g.,
&gt;&gt; java -jar RD_run.jar ./config/conf1.xml

- The output files of NK_run are stored in the same directory as the jar file.
- The output files are txt files. One txt file is for one strategy type under one case. In other words, each output file corresponds to one &lt;strategy&gt; element node in the xml config file.
- The output txt file name is formatted as:
CLOSED, ALLIANCE_MAX, ALLIANCE_MIN:
"o_n" + the number N + "k" + the number K + "_x" + the total number of innovators + "_" + the strategy type + ".txt"
OUTSOURCING, LICENSING:
"o_n" + the number N + "k" + the number K + "_x" + the total number of innovators + "y" + the total number of providers + "_" + the strategy type + ".txt"
- The format in the output txt file is as follows:
SEED, TIMESTAMP, AGENT_TYPE, AGENT_ID, AGENT_POWER, AGENT_STAGE, PERFORMANCE, AGENT_PARTNER	

- IMPORTANT NOTE 1
There are more than one influence matrices with the same N and K. Maybe you will differentiate them with different file names, e.g., "n4k2_1.txt", "n4k2_2.txt". However, the output file name could be the same, because of the format above. Therefore, please rename the generated files if necessary.

- IMPORTANT NOTE 2
New generated output file will not rewrite the original file (if existed) with the same name. Instead, the contents will be appended to the original file (if existed).

