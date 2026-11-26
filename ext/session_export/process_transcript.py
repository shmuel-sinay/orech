"""Strips base64 image data from the exported transcript (keeps all text/tool-use/tool-result
content), and separately extracts an ordered, human-readable log of every Bash/PowerShell
command that was run. Run with: python -I process_transcript.py
"""
import json

IN = 'transcript.jsonl'
OUT_CLEAN = 'transcript_clean.jsonl'
OUT_CMDS = 'commands_log.md'

def strip_images(obj):
    if isinstance(obj, dict):
        if obj.get('type') == 'image' and isinstance(obj.get('source'), dict) and 'data' in obj['source']:
            n = len(obj['source']['data'])
            obj['source']['data'] = f'[omitted, {n} base64 chars]'
            return obj
        return {k: strip_images(v) for k, v in obj.items()}
    if isinstance(obj, list):
        return [strip_images(v) for v in obj]
    return obj

commands = []
cleaned_lines = []
with open(IN, encoding='utf-8') as f:
    for line in f:
        if not line.strip():
            continue
        d = json.loads(line)
        msg = d.get('message')
        if isinstance(msg, dict):
            content = msg.get('content')
            if isinstance(content, list):
                for block in content:
                    if isinstance(block, dict) and block.get('type') == 'tool_use' and block.get('name') in ('Bash', 'PowerShell'):
                        inp = block.get('input', {})
                        commands.append({
                            'tool': block.get('name'),
                            'description': inp.get('description', ''),
                            'command': inp.get('command', ''),
                        })
        cleaned_lines.append(json.dumps(strip_images(d), ensure_ascii=False))

with open(OUT_CLEAN, 'w', encoding='utf-8', newline='\n') as f:
    f.write('\n'.join(cleaned_lines) + '\n')

with open(OUT_CMDS, 'w', encoding='utf-8', newline='\n') as f:
    f.write('# Ordered command log (extracted from the session transcript)\n\n')
    f.write(f'{len(commands)} Bash/PowerShell invocations, in order.\n\n')
    for i, c in enumerate(commands, 1):
        f.write(f"## {i}. {c['tool']}{': ' + c['description'] if c['description'] else ''}\n")
        f.write('```\n' + c['command'] + '\n```\n\n')

print('commands:', len(commands))
print('clean size:', len(('\n'.join(cleaned_lines)).encode()))
