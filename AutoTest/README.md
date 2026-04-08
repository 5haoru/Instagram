# Instagram Simulator - GUIAgent AutoTest Framework

## Overview

This framework provides 40 test instructions (difficulty: easy → expert) for evaluating GUIAgent performance on the Instagram Simulator app.

## File Structure

```
AutoTest/
├── test_instructions.json     # 40 test cases with instructions & verification rules
├── test_runner.py             # Core framework: ADB control, UI parsing, verification
├── gui_agent_adapter.py       # Adapter interfaces for GUIAgent integration
├── scripted_actions.py        # Predefined action sequences for each test
├── run_scripted_tests.py      # Entry point: run with ScriptedAgent
├── run_manual_tests.py        # Entry point: run in manual/interactive mode
└── README.md
```

## Test Difficulty Distribution

| Level  | IDs   | Count | Description                                    |
|--------|-------|-------|------------------------------------------------|
| Easy   | 1-10  | 10    | Single-step: tab navigation, like, save, swipe |
| Medium | 11-20 | 10    | Two-step: navigate + interact, search, open    |
| Hard   | 21-30 | 10    | Multi-step: comment, message, settings toggle  |
| Expert | 31-40 | 10    | Complex workflows: post creation, cross-screen |

## Quick Start

### Prerequisites
- Python 3.8+
- ADB installed and device/emulator connected
- Instagram Simulator app installed on device

### Run All Tests (Manual Mode)
```bash
cd AutoTest
python run_manual_tests.py
```

### Run Tests by Difficulty
```bash
python run_scripted_tests.py --difficulty easy
python run_scripted_tests.py --difficulty medium
python run_scripted_tests.py --difficulty hard
python run_scripted_tests.py --difficulty expert
```

### Run Specific Tests
```bash
python run_scripted_tests.py --ids 1 5 10 21 31
```

### Custom GUIAgent Integration

```python
from test_runner import TestRunner
from gui_agent_adapter import ScreenshotBasedAgent

class MyGUIAgent(ScreenshotBasedAgent):
    def _get_action_from_model(self, instruction, screenshot_path, ui_xml, step):
        # Send screenshot + instruction to your model
        # Return action dict: {"action": "tap", "x": 100, "y": 200}
        # Return {"action": "done"} when complete
        pass

agent = MyGUIAgent()
runner = TestRunner()
tests = runner.load_tests("test_instructions.json")
runner.setup()
runner.run_tests(tests, gui_agent_callback=agent.execute)
runner.print_report()
runner.save_report()
```

## Test Report

Reports are saved as JSON with per-test results and difficulty breakdown:

```json
{
  "total": 40,
  "passed": 35,
  "failed": 3,
  "errors": 2,
  "pass_rate": 87.5,
  "test_cases": [...]
}
```
