from collections import Counter
import nltk


def get_line_entry(i, lines, rel_pos, is_pos_file):
    line = lines[i]
    if line == '':
        return ''
    line_split = line.split('\t')
    word = line_split[0]
    tag = line_split[1]
    if not is_pos_file:
        chunk = line_split[2]
    entry = "\t" + rel_pos + "_tag=" + tag + "\t" + rel_pos + "_word=" + word
    if not is_pos_file:
        entry += "\t" + rel_pos + "_chunk=" + chunk
    else:
        if rel_pos == 'previous':
            entry += "\t" + rel_pos + "_chunk=@@"
        if rel_pos == 'p_previous':
            entry += "\t" + rel_pos + "_chunk=&&"
    return entry


def create_feature_file(input_path, result_path):
    input_pointer = open(input_path)
    result_pointer = open(result_path, 'w')
    is_pos_file = True
    if input_path.endswith('pos-chunk'):
        is_pos_file = False
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
            current_tag = line_split[1]
            if not is_pos_file:
                current_chunk = line_split[2]
            line = current_word + "\tcurrent_tag=" + current_tag
            #line = line + "\tend_ing=" + str(current_word.endswith('ing'))
            #line = line + "\tend_ed=" + str(current_word.endswith('ed'))
            #line = line + "\tend_al=" + str(current_word.endswith('al'))
            #line = line + "\tend_ion=" + str(current_word.endswith('ion'))
            line = line + "\tend_s=" + str(current_word.endswith('s'))
            line = line + "\tstart_title=" + str(current_word.istitle())
            line = line + "\tstart_ucase=" + str(current_word.isupper())
            line = line + "\tcount=" + str(len(current_word))
            line = line + "\thiphen" + str('-' in current_word)
            if i > 0:
                line += get_line_entry(i-1, lines, 'previous', is_pos_file)
            if i > 1:
                line += get_line_entry(i-2, lines, 'p_previous', is_pos_file)
            if i < (total_lines - 1):
                line += get_line_entry(i+1, lines, 'next', is_pos_file)
            if i < (total_lines - 2):
                line += get_line_entry(i+2, lines, 'n_next', is_pos_file)
            if not is_pos_file:
                line = line + "\t" + current_chunk
        else:
            line = ''
        result_pointer.write(line)
        if i != (total_lines -1):
            result_pointer.write('\n')
    input_pointer.close()
    result_pointer.close()


def get_dict_for_line_entry(line):
    splits = line.split('\t')
    dic = Counter({})
    dic['word'] = splits[0]
    split_length = len(splits)
    for i in range(1, split_length - 1):
        feature_split = splits[i].split('=')
        dic[feature_split[0]] = feature_split[1]
    return dic


def read_training_data_features(file_path):
    f = open(file_path)
    data = []
    lines = f.read().split('\n')
    for line in lines:
        if line == '':
            continue
        dic = get_dict_for_line_entry(line)
        data.append((dic, line.split('\t')[len(dic)]))
    return data


def write_results(test_feature_file, result_file, classifier):
    tf = open(test_feature_file)
    r = open(result_file, 'w')
    lines = tf.read().split('\n')
    file_length = len(lines)
    previous_chunk = None
    for index, line in enumerate(lines):
        entry = ''
        if line != '':
            split = line.split('\t')
            dic = get_dict_for_line_entry(line)
            if not previous_chunk == None:
                dic['previous_chunk'] = previous_chunk
            previous_chunk = classifier.prob_classify(dic).max()
            entry = split[0] + '\t' + previous_chunk
        else:
            entry = ''
        r.write(entry)
        if index != (file_length -1):
            r.write('\n')
    tf.close()
    r.close()


def main():
    training = "D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\\WSJ_02-21.pos-chunk"
    training_feature_file = "D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\\features_training.feature"
    test = "D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\WSJ_23.pos"
    test_feature_file = "D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\\features_test.feature"
    result_file = "D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\\result.chunk"
    create_feature_file(training, training_feature_file)
    features = read_training_data_features(training_feature_file)
    classfier = nltk.classify.MaxentClassifier.train(features, 'IIS', trace=0, max_iter=1)
    create_feature_file(test, test_feature_file)
    write_results(test_feature_file, result_file, classfier)
    score("D:\Academics\Courses\NLP\hw5_programming\WSJ_CHUNK_CORPUS_FOR_STUDENTS\WSJ_24.chunk", result_file)

def score(keyFileName, responseFileName):
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
        keyTag = keyFields[1][0:1]
        responseFields = response[i].split('\t')
        if len(responseFields) != 2:
            print "format error at line " + str(i)
            exit()
        responseToken = responseFields[0]
        responseTag = responseFields[1][0:1]
        if responseToken != keyToken:
            print "token mismatch at line " + str(i)
            exit()
        if responseTag == keyTag:
            correct = correct + 1
        else:
            incorrect = incorrect + 1
        responseEnd = responseStart != 0 and (responseTag == 'O' or responseTag == 'B')
        responseBegin = responseTag == 'B' or (responseStart == 0 and responseTag == 'I')
        keyBegin = keyTag == 'B' or (keyStart == 0 and keyTag == 'I')
        keyEnd = keyStart != 0 and (keyTag == 'O' or keyTag == 'B')
        if responseEnd:
            responseGroupCount = responseGroupCount + 1
        if keyEnd:
            keyGroupCount = keyGroupCount + 1
        if responseEnd and keyEnd and responseStart == keyStart:
            correctGroupCount = correctGroupCount + 1
        if responseBegin:
            responseStart = i
        elif responseEnd:
            responseStart = 0
        if keyBegin:
            keyStart = i
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
    F = 2 * precision * recall / (precision + recall)
    print "  precision: %5.2f" % precision
    print "  recall:    %5.2f" % recall
    print "  F1:        %5.2f" % F


if __name__ == "__main__":
    main()
