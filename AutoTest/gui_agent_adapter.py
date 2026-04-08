"""
GUIAgent Adapter - Interface for connecting GUIAgent models to the test framework.
==================================================================================
This module provides the adapter interface that GUIAgent implementations should use
to integrate with the AutoTest framework.

Usage:
    1. Implement a concrete GUIAgentAdapter subclass
    2. Pass its `execute` method as gui_agent_callback to TestRunner.run_tests()
"""

import time
from abc import ABC, abstractmethod
from test_runner import ADBController, UIParser


class GUIAgentAdapter(ABC):
    """
    Abstract base class for GUIAgent adapters.

    Subclass this and implement `execute()` to connect your GUIAgent model
    to the test framework.
    """

    @abstractmethod
    def execute(self, instruction: str, adb: ADBController):
        """
        Execute a natural language instruction on the device.

        Args:
            instruction: The natural language instruction to execute
                         (e.g., "Click the search tab in the bottom navigation bar")
            adb: ADBController instance for interacting with the device

        The agent should:
            1. Take a screenshot to observe the current UI state
            2. Parse the instruction to determine what action to take
            3. Execute the action using adb methods (tap, swipe, input_text, etc.)
            4. Repeat until the instruction is fully executed
        """
        pass


class ManualAgent(GUIAgentAdapter):
    """Manual testing adapter - prompts the user to perform actions."""

    def execute(self, instruction: str, adb: ADBController):
        print(f"\n  Instruction: {instruction}")
        input("  >> Perform the action manually, then press Enter...")


class ScriptedAgent(GUIAgentAdapter):
    """
    Scripted agent that executes predefined action sequences.
    Useful for regression testing with known UI layouts.
    """

    def __init__(self, action_scripts: dict = None):
        """
        Args:
            action_scripts: Dict mapping test_id -> list of action dicts
                Each action dict: {"action": "tap"|"swipe"|"type"|"back"|"wait",
                                   ...action-specific params}
        """
        self.scripts = action_scripts or {}

    def execute(self, instruction: str, adb: ADBController):
        # Find matching script by instruction text
        actions = self.scripts.get(instruction, [])
        if not actions:
            print(f"  [WARN] No script found for: {instruction}")
            return

        for action in actions:
            self._execute_action(action, adb)

    def _execute_action(self, action: dict, adb: ADBController):
        act_type = action.get("action", "")

        if act_type == "tap":
            adb.tap(action["x"], action["y"])
        elif act_type == "tap_text":
            xml = adb.get_ui_xml()
            ui = UIParser(xml)
            nodes = ui.find_by_text(action["text"])
            if not nodes:
                nodes = ui.find_by_text_contains(action["text"])
            if nodes:
                node = nodes[0]
                adb.tap(node.get("center_x", 0), node.get("center_y", 0))
            else:
                print(f"    [WARN] Element with text '{action['text']}' not found")
        elif act_type == "tap_desc":
            xml = adb.get_ui_xml()
            ui = UIParser(xml)
            nodes = ui.find_by_content_desc(action["desc"])
            if nodes:
                node = nodes[0]
                adb.tap(node.get("center_x", 0), node.get("center_y", 0))
            else:
                print(f"    [WARN] Element with desc '{action['desc']}' not found")
        elif act_type == "swipe":
            adb.swipe(action["x1"], action["y1"], action["x2"], action["y2"],
                      action.get("duration", 300))
        elif act_type == "swipe_up":
            adb.swipe_up()
        elif act_type == "swipe_down":
            adb.swipe_down()
        elif act_type == "type":
            adb.input_text(action["text"])
        elif act_type == "clear":
            adb.clear_text(action.get("length", 50))
        elif act_type == "back":
            adb.press_back()
        elif act_type == "enter":
            adb.press_enter()
        elif act_type == "wait":
            time.sleep(action.get("seconds", 1))
        else:
            print(f"    [WARN] Unknown action type: {act_type}")


class ScreenshotBasedAgent(GUIAgentAdapter):
    """
    Template for screenshot-based GUIAgent.
    Takes screenshots, sends to a vision model, and executes returned actions.

    Subclass this and implement `_get_action_from_model()` to connect your model.
    """

    def __init__(self, max_steps: int = 20, step_delay: float = 1.0):
        self.max_steps = max_steps
        self.step_delay = step_delay

    def execute(self, instruction: str, adb: ADBController):
        for step in range(self.max_steps):
            # 1. Capture current screen
            screenshot_path = f"/tmp/agent_step_{step}.png"
            adb.take_screenshot(screenshot_path)

            # 2. Get UI hierarchy
            xml = adb.get_ui_xml()

            # 3. Ask model what to do
            action = self._get_action_from_model(
                instruction=instruction,
                screenshot_path=screenshot_path,
                ui_xml=xml,
                step=step,
            )

            if action is None or action.get("action") == "done":
                print(f"    Agent completed in {step + 1} steps")
                break

            # 4. Execute the action
            self._execute_model_action(action, adb)
            time.sleep(self.step_delay)
        else:
            print(f"    [WARN] Agent reached max steps ({self.max_steps})")

    @abstractmethod
    def _get_action_from_model(
        self, instruction: str, screenshot_path: str, ui_xml: str, step: int
    ) -> dict:
        """
        Send the current state to the model and get the next action.

        Args:
            instruction: The original instruction
            screenshot_path: Path to current screenshot
            ui_xml: UIAutomator XML dump
            step: Current step number

        Returns:
            Action dict: {"action": "tap"|"swipe"|"type"|"back"|"done", ...params}
            Return {"action": "done"} when the instruction is complete.
        """
        pass

    def _execute_model_action(self, action: dict, adb: ADBController):
        act = action.get("action", "")
        if act == "tap":
            adb.tap(action["x"], action["y"])
        elif act == "swipe":
            adb.swipe(action["x1"], action["y1"], action["x2"], action["y2"])
        elif act == "swipe_up":
            adb.swipe_up()
        elif act == "swipe_down":
            adb.swipe_down()
        elif act == "type":
            adb.input_text(action["text"])
        elif act == "back":
            adb.press_back()
        elif act == "enter":
            adb.press_enter()
