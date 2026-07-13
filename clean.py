import os, re

def strip_comments(text):
    pattern = re.compile(r'(\"\"\"[\s\S]*?\"\"\")|(\"(?:\\.|[^\"\\])*\")|(/\*[\s\S]*?\*/)|(//[^\n]*)')
    def replacer(match):
        if match.group(1) is not None:
            return match.group(1)
        elif match.group(2) is not None:
            return match.group(2)
        else:
            return ''
    return pattern.sub(replacer, text)

def clean_empty_lines(text):
    lines = text.split('\n')
    cleaned = []
    for line in lines:
        if line.strip() == '' and (not cleaned or cleaned[-1].strip() == ''):
            continue
        cleaned.append(line)
    return '\n'.join(cleaned)

root_dir = r'c:\Users\prave\Desktop\AIIC'
count = 0

for subdir, dirs, files in os.walk(root_dir):
    dirs[:] = [d for d in dirs if d not in ['.git', '.gradle', 'build', 'app\\build']]
    for file in files:
        if file.endswith('.kt') or file.endswith('.java') or file.endswith('.kts') or file.endswith('.xml'):
            filepath = os.path.join(subdir, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                if file.endswith('.xml'):
                    new_content = re.sub(r'<!--[\s\S]*?-->', '', content)
                else:
                    new_content = strip_comments(content)
                    
                new_content = clean_empty_lines(new_content)
                
                if new_content != content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    count += 1
            except Exception:
                pass

print(f'Stripped comments from {count} additional files.')
