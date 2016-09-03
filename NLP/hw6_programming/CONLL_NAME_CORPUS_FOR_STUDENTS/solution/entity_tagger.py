from collections import Counter
import nltk


def get_line_entry(i, lines, rel_pos, is_pos_file):
    line = lines[i]
    word = ''
    pos = ''
    chunk = ''
    entity = ''
    if line != '':
        line_split = line.split('\t')

        word = line_split[0]
        pos = line_split[1]
        chunk = line_split[2]
        if not is_pos_file:
            entity = line_split[3]

    else:
        word = 'None'
        pos = 'None'
        chunk = 'None'
        entity = 'None'


    entry = "\t" + rel_pos + "_word=" + word #+ "\t" + rel_pos + "_chunk=" + chunk
    if rel_pos == 'next':
        entry = entry + "\t" + rel_pos + "_pos=" + pos

    if not is_pos_file:
        entry += "\t" + rel_pos + "_entity=" + entity
    else:
        if rel_pos == 'previous':
            entry += "\t" + rel_pos + "_entity=@@"
        #if rel_pos == 'p_previous':
        #    entry += "\t" + rel_pos + "_entity=&&"
    return entry


def create_feature_file(input_path, result_path):
    input_pointer = open(input_path)
    result_pointer = open(result_path, 'w')
    is_pos_file = False
    if input_path.endswith('pos-chunk'):
        is_pos_file = True
    lines = input_pointer.read().split('\n')
    # previous_tag = ''
    # previous_word = ''
    # next_tag = ''
    # next_word = ''
    line = ''
    total_lines = len(lines)
    for i in range(0, total_lines):
        if lines[i] != '':
            line_split = lines[i].split('\t')
            current_word = line_split[0]
            current_pos = line_split[1]
            current_chunk = line_split[2]
            if not is_pos_file:
                current_entity = line_split[3]
            line = current_word + "\tcurrent_pos=" + current_pos
            line = line + "\tcurrent_chunk=" + current_chunk
            #line = line + "\tend_ing=" + str(current_word.endswith('ing'))
            #line = line + "\tend_ed=" + str(current_word.endswith('ed'))
            #line = line + "\tend_al=" + str(current_word.endswith('al'))
            #line = line + "\tend_ion=" + str(current_word.endswith('ion'))
            #line = line + "\tend_s=" + str(current_word.endswith('s'))
            line = line + "\tstart_title=" + str(current_word.istitle())
            line = line + "\tstart_ucase=" + str(current_word.isupper())
            #line = line + "\tcount=" + str(len(current_word))
            line = line + "\tpresent_in_names=" + str(all_names.get(current_word.lower(), 0))
            line = line+"\tpresent_in_country="+str(all_countries.get(current_word.lower(),0))
            #line = line + "\thiphen" + str('-' in current_word)
            if i > 0:
                line += get_line_entry(i-1, lines, 'previous', is_pos_file)
            #if i > 1:
            #    line += get_line_entry(i-2, lines, 'p_previous', is_pos_file)
            if i < (total_lines - 1):
                line += get_line_entry(i+1, lines, 'next', is_pos_file)
            #if i < (total_lines - 2):
            #    line += get_line_entry(i+2, lines, 'n_next', is_pos_file)

            if not is_pos_file:
                line = line + "\t" + current_entity
        else:
            line = ''
        result_pointer.write(line)
        if i != (total_lines -1):
            result_pointer.write('\n')
    input_pointer.close()
    result_pointer.close()


def read_all_names(file_path):
    input_pointer = open(file_path)
    lines = input_pointer.read().split('\n')
    total_lines = len(lines)
    for i in range(0, total_lines):
        if lines[i] != '':
            line_split = lines[i].split(' ')
            all_names[line_split[0].lower()] = 1
all_names = dict({})


def read_all_countries(file_path):
    input_file = open(file_path)
    lines = input_file.read().split('\n')
    lines_len = len(lines)
    for i in range(0, lines_len):
        if lines[i] != '':
            line_split = lines[i].split(' ')
            all_countries[line_split[0].lower()] = 1
all_countries = dict({})


def main():
    training = "D:\Academics\Courses\NLP\hw6_programming\CONLL_NAME_CORPUS_FOR_STUDENTS\CONLL_train.pos-chunk-name"
    training_feature_file = "D:\Academics\Courses\NLP\hw6_programming\CONLL_NAME_CORPUS_FOR_STUDENTS\\features_training.feature"
    test = "D:\Academics\Courses\NLP\hw6_programming\CONLL_NAME_CORPUS_FOR_STUDENTS\CONLL_dev.pos-chunk"
    test_feature_file = "D:\Academics\Courses\NLP\hw6_programming\CONLL_NAME_CORPUS_FOR_STUDENTS\\features_test.feature"
    result_file = "D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//solution//ans";
    last_name = "D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//dist.all.last";
    male_name = "D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//dist.male.first";
    female_name = "D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//dist.female.first";
    country_name= "D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//countries.txt";

    read_all_names(last_name)
    read_all_names(male_name)
    read_all_names(female_name)
    read_all_countries(country_name)
    #create_feature_file(training, training_feature_file)
    #features = read_training_data_features(training_feature_file)
    #classfier = nltk.classify.MaxentClassifier.train(features, 'IIS', trace=0, max_iter=1)
    #create_feature_file(test, test_feature_file)
    #write_results(test_feature_file, result_file, classfier)
    score("D://Academics//Courses//NLP//hw6_programming//CONLL_NAME_CORPUS_FOR_STUDENTS//CONLL_dev.name", result_file)

def score (keyFileName, responseFileName):
	keyFile = open(keyFileName, 'r')
	key = keyFile.readlines()
	responseFile = open(responseFileName, 'r')
	response = responseFile.readlines()
	if len(key) != len(response):
    		print "length mismatch between key and submitted file"
		exit()
	correct = 0
	incorrect = 0
	keyGroupCount = 0
	keyStart = 0
	responseGroupCount = 0
	responseStart = 0
	correctGroupCount = 0
	for i in range(len(key)):
		key[i] = key[i].rstrip('\n')
		response[i] = response[i].rstrip('\n')
		if key[i] == "":
			if response[i] == "":
				continue
			else:
    				print "sentence break expected at line " + str(i)
				exit()
    		keyFields = key[i].split('\t')
		if len(keyFields) != 2:
    			print "format error in key at line " + str(i) + ":" + key[i]
			exit()
		keyToken = keyFields[0]
		keyTag = keyFields[1]
    		responseFields = response[i].split('\t')
		if len(responseFields) != 2:
    			print "format error at line " + str(i)
			exit()
		responseToken = responseFields[0]
		responseTag = responseFields[1]
    		if responseToken != keyToken:
    			print "token mismatch at line " + str(i)
			exit()
		if responseTag == keyTag:
			correct = correct + 1
		else:
			incorrect = incorrect + 1
                # the previous token ends a group if
                #   we are in a group AND
                #   the current tag is O OR the current tag is a B tag
                #   the current tag is an I tag with a different type from the current group
		responseEnd =  responseStart!=0 and (responseTag=='O' or responseTag[0:1]=='B' or (responseTag[0:1]=='I' and responseTag[2:]!=responseGroupType))
                # the current token begins a group if
                #   the previous token was not in a group or ended a group AND
                #   the current tag is an I or B tag
		responseBegin = (responseStart==0 or responseEnd) and (responseTag[0:1]=='B' or responseTag[0:1]=='I')
		keyEnd =  keyStart!=0 and (keyTag=='O' or keyTag[0:1]=='B' or (keyTag[0:1]=='I' and keyTag[2:]!=keyGroupType))
		keyBegin = (keyStart==0 or keyEnd) and (keyTag[0:1]=='B' or keyTag[0:1]=='I')
		if responseEnd:
		    responseGroupCount = responseGroupCount + 1
		if keyEnd:
		    keyGroupCount = keyGroupCount + 1
		if responseEnd and keyEnd and responseStart == keyStart and responseGroupType == keyGroupType:
		    correctGroupCount = correctGroupCount + 1
		if responseBegin:
		    responseStart = i
		    responseGroupType = responseTag[2:]
		elif responseEnd:
		    responseStart = 0
		if keyBegin:
		    keyStart = i
		    keyGroupType = keyTag[2:]
		elif keyEnd:
		    keyStart = 0
	print correct, "out of", str(correct + incorrect) + " tags correct"
	accuracy = 100.0 * correct / (correct + incorrect)
	print "  accuracy: %5.2f" % accuracy
	print keyGroupCount, "groups in key"
	print responseGroupCount, "groups in response"
	print correctGroupCount, "correct groups"
	precision = 100.0 * correctGroupCount / responseGroupCount
	recall = 100.0 * correctGroupCount / keyGroupCount
	F = 2 * precision  * recall / (precision + recall)
	print "  precision: %5.2f" % precision
	print "  recall:    %5.2f" % recall
	print "  F1:        %5.2f" % F

if __name__ == "__main__":
    main()
