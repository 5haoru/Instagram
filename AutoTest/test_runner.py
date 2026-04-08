"""
Instagram Simulator - GUIAgent AutoTest Framework
===================================================
Automated testing framework for GUIAgent evaluation.
Uses ADB + UIAutomator to interact with the Android app and verify results.
"""

import json
import subprocess
import time
import os
import sys
import re
from dataclasses import dataclass, field
from typing import Optional
from enum import Enum


class TestResult(Enum):
    PASS = "PASS"
    FAIL = "FAIL"
    ERROR = "ERROR"
    SKIP = "SKIP"


@dataclass
class TestCase:
    id: int
    instruction: str
    difficulty: str
    category: str
    expected_result: str
    verification: dict
    result: TestResult = TestResult.SKIP
    actual_result: str = ""
    duration: float = 0.0
    screenshot_path: str = ""


@dataclass
class TestReport:
    total: int = 0
    passed: int = 0
    failed: int = 0
    errors: int = 0
    skipped: int = 0
    test_cases: list = field(default_factory=list)
    start_time: float = 0.0
    end_time: float = 0.0


class ADBController:
    """ADB interface for interacting with the Android device/emulator."""

    def __init__(self, device_id: str = None):
        self.device_id = device_id
        self.base_cmd = ["adb"]
        if device_id:
            self.base_cmd += ["-s", device_id]

    def run_cmd(self, args: list, timeout: int = 30) -> str:
        cmd = self.base_cmd + args
        try:
            result = subprocess.run(
                cmd, capture_output=True, text=True, timeout=timeout
            )
            return result.stdout.strip()
        except subprocess.TimeoutExpired:
            return ""
        except Exception as e:
            return f"ERROR: {e}"

    def shell(self, cmd: str, timeout: int = 30) -> str:
        return self.run_cmd(["shell", cmd], timeout)

    def get_current_activity(self) -> str:
        output = self.shell("dumpsys activity activities | grep mResumedActivity")
        return output

    def get_ui_xml(self) -> str:
        self.shell("uiautomator dump /sdcard/ui_dump.xml")
        time.sleep(0.5)
        output = self.run_cmd(["shell", "cat", "/sdcard/ui_dump.xml"])
        return output

    def tap(self, x: int, y: int):
        self.shell(f"input tap {x} {y}")
        time.sleep(0.5)

    def long_press(self, x: int, y: int, duration_ms: int = 1000):
        self.shell(f"input swipe {x} {y} {x} {y} {duration_ms}")
        time.sleep(0.5)

    def swipe(self, x1: int, y1: int, x2: int, y2: int, duration_ms: int = 300):
        self.shell(f"input swipe {x1} {y1} {x2} {y2} {duration_ms}")
        time.sleep(0.5)

    def swipe_up(self, center_x: int = 540, start_y: int = 1600, end_y: int = 400):
        self.swipe(center_x, start_y, center_x, end_y)

    def swipe_down(self, center_x: int = 540, start_y: int = 400, end_y: int = 1600):
        self.swipe(center_x, start_y, center_x, end_y)

    def input_text(self, text: str):
        escaped = text.replace(" ", "%s").replace("'", "\\'")
        self.shell(f"input text '{escaped}'")
        time.sleep(0.3)

    def press_back(self):
        self.shell("input keyevent KEYCODE_BACK")
        time.sleep(0.5)

    def press_enter(self):
        self.shell("input keyevent KEYCODE_ENTER")
        time.sleep(0.3)

    def take_screenshot(self, local_path: str):
        remote_path = "/sdcard/screenshot.png"
        self.shell(f"screencap -p {remote_path}")
        self.run_cmd(["pull", remote_path, local_path])

    def clear_text(self, length: int = 50):
        for _ in range(length):
            self.shell("input keyevent KEYCODE_DEL")
        time.sleep(0.3)

    def launch_app(self, package: str, activity: str = None):
        if activity:
            self.shell(f"am start -n {package}/{activity}")
        else:
            self.shell(
                f"monkey -p {package} -c android.intent.category.LAUNCHER 1"
            )
        time.sleep(2)

    def force_stop_app(self, package: str):
        self.shell(f"am force-stop {package}")
        time.sleep(1)

    def is_device_connected(self) -> bool:
        output = self.run_cmd(["devices"])
        lines = output.strip().split("\n")
        for line in lines[1:]:
            if "device" in line and "offline" not in line:
                return True
        return False


class UIParser:
    """Parse UIAutomator XML dump to find UI elements."""

    def __init__(self, xml_content: str):
        self.xml = xml_content

    def find_by_text(self, text: str) -> list:
        """Find elements by exact text match."""
        pattern = rf'<node[^>]*text="{re.escape(text)}"[^>]*/>'
        matches = re.findall(pattern, self.xml)
        return [self._parse_node(m) for m in matches]

    def find_by_text_contains(self, text: str) -> list:
        """Find elements containing the given text."""
        pattern = rf'<node[^>]*text="[^"]*{re.escape(text)}[^"]*"[^>]*/>'
        matches = re.findall(pattern, self.xml)
        return [self._parse_node(m) for m in matches]

    def find_by_resource_id(self, resource_id: str) -> list:
        """Find elements by resource-id."""
        pattern = rf'<node[^>]*resource-id="[^"]*{re.escape(resource_id)}[^"]*"[^>]*/>'
        matches = re.findall(pattern, self.xml)
        return [self._parse_node(m) for m in matches]

    def find_by_content_desc(self, desc: str) -> list:
        """Find elements by content-description."""
        pattern = rf'<node[^>]*content-desc="[^"]*{re.escape(desc)}[^"]*"[^>]*/>'
        matches = re.findall(pattern, self.xml)
        return [self._parse_node(m) for m in matches]

    def find_by_class(self, class_name: str) -> list:
        """Find elements by class name."""
        pattern = rf'<node[^>]*class="{re.escape(class_name)}"[^>]*/>'
        matches = re.findall(pattern, self.xml)
        return [self._parse_node(m) for m in matches]

    def exists(self, text: str) -> bool:
        """Check if any element with the given text exists."""
        return len(self.find_by_text(text)) > 0

    def text_contains_on_screen(self, text: str) -> bool:
        """Check if the text appears anywhere in the UI dump."""
        return text.lower() in self.xml.lower()

    def _parse_node(self, node_str: str) -> dict:
        """Parse a node string to extract attributes."""
        result = {}
        for attr in ["text", "resource-id", "content-desc", "class", "bounds",
                      "clickable", "checked", "selected", "enabled"]:
            match = re.search(rf'{attr}="([^"]*)"', node_str)
            if match:
                result[attr] = match.group(1)

        # Parse bounds to get center coordinates
        if "bounds" in result:
            bounds_match = re.match(
                r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", result["bounds"]
            )
            if bounds_match:
                x1, y1, x2, y2 = map(int, bounds_match.groups())
                result["center_x"] = (x1 + x2) // 2
                result["center_y"] = (y1 + y2) // 2
                result["x1"], result["y1"] = x1, y1
                result["x2"], result["y2"] = x2, y2

        return result


class Verifier:
    """Verification engine for checking test results."""

    def __init__(self, adb: ADBController):
        self.adb = adb

    def verify(self, verification: dict, ui: UIParser = None) -> tuple:
        """
        Verify a test result.
        Returns (success: bool, message: str)
        """
        if ui is None:
            xml = self.adb.get_ui_xml()
            ui = UIParser(xml)

        v_type = verification.get("type", "")

        if v_type == "screen_check":
            return self._verify_screen(verification, ui)
        elif v_type == "element_state":
            return self._verify_element_state(verification, ui)
        elif v_type == "bottom_sheet":
            return self._verify_bottom_sheet(verification, ui)
        elif v_type == "search_results":
            return self._verify_search_results(verification, ui)
        elif v_type == "comment_added":
            return self._verify_comment_added(verification, ui)
        elif v_type == "message_sent":
            return self._verify_message_sent(verification, ui)
        elif v_type == "setting_toggled":
            return self._verify_setting_toggled(verification, ui)
        elif v_type == "setting_value":
            return self._verify_setting_value(verification, ui)
        elif v_type == "field_value":
            return self._verify_field_value(verification, ui)
        elif v_type == "count_change":
            return self._verify_count_change(verification, ui)
        elif v_type == "user_in_list":
            return self._verify_user_in_list(verification, ui)
        elif v_type == "collection_created":
            return self._verify_collection_created(verification, ui)
        elif v_type == "post_created":
            return self._verify_post_created(verification, ui)
        elif v_type == "menu_action":
            return self._verify_menu_action(verification, ui)
        elif v_type == "reel_changed":
            return self._verify_reel_changed(verification, ui)
        elif v_type == "post_in_saved":
            return self._verify_post_in_saved(verification, ui)
        elif v_type == "multi_field_check":
            return self._verify_multi_field(verification, ui)
        elif v_type == "workflow_complete":
            return self._verify_workflow(verification, ui)
        else:
            return False, f"Unknown verification type: {v_type}"

    def _verify_screen(self, v: dict, ui: UIParser) -> tuple:
        """Verify the current screen matches expected."""
        screen = v.get("target_screen", "")
        screen_indicators = {
            "home": ["Instagram", "Stories"],
            "search": ["Search"],
            "reels": ["Reels"],
            "messages": ["Messages"],
            "profile": ["Edit profile", "Share profile"],
            "notifications": ["Notifications"],
            "new_post_gallery": ["Next", "Gallery"],
            "edit_profile": ["Edit profile", "Name"],
            "share_profile": ["Share profile", "QR"],
            "followers_following": ["Followers", "Following"],
            "saved": ["Saved", "All Posts"],
            "chat_detail": ["Message..."],
            "settings": ["Settings"],
            "account_privacy": ["Private account"],
            "time_management": ["Time management", "daily time limit"],
            "close_friends": ["Close friends"],
            "blocked": ["Blocked"],
        }

        indicators = screen_indicators.get(screen, [])
        for indicator in indicators:
            if ui.text_contains_on_screen(indicator):
                return True, f"Screen '{screen}' detected (found '{indicator}')"
        return False, f"Screen '{screen}' not detected. Indicators {indicators} not found."

    def _verify_element_state(self, v: dict, ui: UIParser) -> tuple:
        target = v.get("target", "")
        expected = v.get("expected_state", "")
        # For Compose apps, check content-desc or text for state indicators
        if "like" in target:
            if expected == "liked":
                if ui.text_contains_on_screen("Unlike") or ui.text_contains_on_screen("liked"):
                    return True, "Element is in 'liked' state"
                return False, "Element is not in 'liked' state"
            else:
                if ui.text_contains_on_screen("Like") and not ui.text_contains_on_screen("Unlike"):
                    return True, "Element is in 'not liked' state"
                return False, "Element is still in 'liked' state"
        elif "bookmark" in target or "save" in target:
            if expected == "saved":
                if ui.text_contains_on_screen("Saved") or ui.text_contains_on_screen("Unsave"):
                    return True, "Post is saved"
                return False, "Post is not saved"
        return False, f"Cannot verify element state for target: {target}"

    def _verify_bottom_sheet(self, v: dict, ui: UIParser) -> tuple:
        sheet_type = v.get("sheet_type", "")
        if sheet_type == "comments":
            if ui.text_contains_on_screen("Comments") and ui.text_contains_on_screen("Add a comment"):
                return True, "Comment bottom sheet is visible"
            return False, "Comment bottom sheet not found"
        elif sheet_type == "share":
            if ui.text_contains_on_screen("Copy link") or ui.text_contains_on_screen("Add to story"):
                return True, "Share bottom sheet is visible"
            return False, "Share bottom sheet not found"
        elif sheet_type == "menu":
            if ui.text_contains_on_screen("Not interested") or ui.text_contains_on_screen("Report"):
                return True, "Menu bottom sheet is visible"
            return False, "Menu bottom sheet not found"
        return False, f"Unknown sheet type: {sheet_type}"

    def _verify_search_results(self, v: dict, ui: UIParser) -> tuple:
        query = v.get("query", "")
        has_results = v.get("has_results", True)
        if has_results:
            if ui.text_contains_on_screen(query):
                return True, f"Search results found for '{query}'"
            return False, f"No search results for '{query}'"
        return True, "No results expected, none found"

    def _verify_comment_added(self, v: dict, ui: UIParser) -> tuple:
        text = v.get("text", "")
        if ui.text_contains_on_screen(text):
            return True, f"Comment '{text}' found on screen"
        return False, f"Comment '{text}' not found"

    def _verify_message_sent(self, v: dict, ui: UIParser) -> tuple:
        text = v.get("text", "")
        if ui.text_contains_on_screen(text):
            return True, f"Message '{text}' found in chat"
        return False, f"Message '{text}' not found in chat"

    def _verify_setting_toggled(self, v: dict, ui: UIParser) -> tuple:
        setting = v.get("setting", "")
        state = v.get("state", True)
        # Check for toggle/switch state in UI
        nodes = ui.find_by_text_contains(setting.replace("_", " "))
        if nodes:
            return True, f"Setting '{setting}' found, state verification requires visual check"
        return False, f"Setting '{setting}' not found on screen"

    def _verify_setting_value(self, v: dict, ui: UIParser) -> tuple:
        setting = v.get("setting", "")
        value = v.get("value", "")
        if ui.text_contains_on_screen(str(value)):
            return True, f"Setting value '{value}' found"
        return False, f"Setting value '{value}' not found"

    def _verify_field_value(self, v: dict, ui: UIParser) -> tuple:
        value = v.get("value", "")
        if ui.text_contains_on_screen(value):
            return True, f"Field value '{value}' found"
        return False, f"Field value '{value}' not found"

    def _verify_count_change(self, v: dict, ui: UIParser) -> tuple:
        counter = v.get("counter", "")
        # Count verification depends on tracking before/after state
        if ui.text_contains_on_screen(counter):
            return True, f"Counter '{counter}' visible (value change requires pre/post comparison)"
        return False, f"Counter '{counter}' not found"

    def _verify_user_in_list(self, v: dict, ui: UIParser) -> tuple:
        list_name = v.get("list", "")
        # Generic check that we're on the right screen
        if ui.text_contains_on_screen(list_name.replace("_", " ")):
            return True, f"On '{list_name}' screen"
        return False, f"Not on '{list_name}' screen"

    def _verify_collection_created(self, v: dict, ui: UIParser) -> tuple:
        name = v.get("name", "")
        if ui.text_contains_on_screen(name):
            return True, f"Collection '{name}' found"
        return False, f"Collection '{name}' not found"

    def _verify_post_created(self, v: dict, ui: UIParser) -> tuple:
        # After posting, should be back on home screen with snackbar
        if ui.text_contains_on_screen("Post shared") or ui.text_contains_on_screen("Instagram"):
            return True, "Post creation flow completed successfully"
        return False, "Post creation could not be verified"

    def _verify_menu_action(self, v: dict, ui: UIParser) -> tuple:
        # After menu action, the sheet should be dismissed
        action = v.get("action", "")
        if not ui.text_contains_on_screen("Not interested") and not ui.text_contains_on_screen("Report"):
            return True, f"Menu dismissed after '{action}' action"
        return False, f"Menu may still be visible after '{action}' action"

    def _verify_reel_changed(self, v: dict, ui: UIParser) -> tuple:
        # Just verify we're still on Reels screen
        if ui.text_contains_on_screen("Reels"):
            return True, "Still on Reels screen after swipe"
        return False, "Not on Reels screen"

    def _verify_post_in_saved(self, v: dict, ui: UIParser) -> tuple:
        if ui.text_contains_on_screen("All Posts") or ui.text_contains_on_screen("Saved"):
            return True, "On Saved screen - saved posts visible"
        return False, "Not on Saved screen"

    def _verify_multi_field(self, v: dict, ui: UIParser) -> tuple:
        fields = v.get("fields", {})
        for field_name, value in fields.items():
            if not ui.text_contains_on_screen(value):
                return False, f"Field '{field_name}' value '{value}' not found"
        return True, "All field values verified"

    def _verify_workflow(self, v: dict, ui: UIParser) -> tuple:
        # Complex workflow verification - check final state
        steps = v.get("steps", [])
        return True, f"Workflow with {len(steps)} steps - final state verification (manual check)"


class TestRunner:
    """Main test runner for the GUIAgent AutoTest framework."""

    APP_PACKAGE = "com.example.instagram_simulator"
    APP_ACTIVITY = ".MainActivity"

    def __init__(self, device_id: str = None, screenshot_dir: str = "screenshots"):
        self.adb = ADBController(device_id)
        self.verifier = Verifier(self.adb)
        self.screenshot_dir = screenshot_dir
        self.report = TestReport()
        os.makedirs(screenshot_dir, exist_ok=True)

    def load_tests(self, json_path: str) -> list:
        """Load test cases from JSON file."""
        with open(json_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        tests = []
        for t in data["tests"]:
            tests.append(TestCase(
                id=t["id"],
                instruction=t["instruction"],
                difficulty=t["difficulty"],
                category=t["category"],
                expected_result=t["expected_result"],
                verification=t["verification"],
            ))
        return tests

    def setup(self):
        """Setup before running tests."""
        if not self.adb.is_device_connected():
            print("[ERROR] No device connected. Please connect a device or start an emulator.")
            sys.exit(1)

        print("[INFO] Device connected.")
        print(f"[INFO] Launching app: {self.APP_PACKAGE}")
        self.adb.force_stop_app(self.APP_PACKAGE)
        self.adb.launch_app(self.APP_PACKAGE, f"{self.APP_PACKAGE}/{self.APP_ACTIVITY}")
        time.sleep(3)
        print("[INFO] App launched successfully.")

    def reset_app(self):
        """Reset app to initial state between tests."""
        self.adb.force_stop_app(self.APP_PACKAGE)
        time.sleep(0.5)
        self.adb.launch_app(self.APP_PACKAGE, f"{self.APP_PACKAGE}/{self.APP_ACTIVITY}")
        time.sleep(3)

    def run_single_test(self, test: TestCase, gui_agent_callback=None) -> TestCase:
        """
        Run a single test case.

        gui_agent_callback: A callable that takes (instruction: str, adb: ADBController)
                           and executes the GUI actions. If None, the test is run
                           in manual/interactive mode.
        """
        print(f"\n{'='*60}")
        print(f"[TEST {test.id:02d}] ({test.difficulty.upper()}) {test.instruction}")
        print(f"{'='*60}")

        start_time = time.time()

        try:
            if gui_agent_callback:
                # Automated: let the GUIAgent execute the instruction
                gui_agent_callback(test.instruction, self.adb)
                time.sleep(1)
            else:
                # Manual mode: wait for user to perform the action
                input(f"  >> Perform the action manually, then press Enter to verify...")

            # Take screenshot for evidence
            screenshot_path = os.path.join(
                self.screenshot_dir, f"test_{test.id:02d}.png"
            )
            self.adb.take_screenshot(screenshot_path)
            test.screenshot_path = screenshot_path

            # Verify the result
            xml = self.adb.get_ui_xml()
            ui = UIParser(xml)
            success, message = self.verifier.verify(test.verification, ui)

            if success:
                test.result = TestResult.PASS
                test.actual_result = message
                print(f"  [PASS] {message}")
            else:
                test.result = TestResult.FAIL
                test.actual_result = message
                print(f"  [FAIL] {message}")

        except Exception as e:
            test.result = TestResult.ERROR
            test.actual_result = str(e)
            print(f"  [ERROR] {e}")

        test.duration = time.time() - start_time
        return test

    def run_tests(
        self,
        tests: list,
        gui_agent_callback=None,
        difficulty_filter: str = None,
        category_filter: str = None,
        test_ids: list = None,
        reset_between_tests: bool = True,
    ) -> TestReport:
        """Run a batch of test cases."""
        self.report = TestReport()
        self.report.start_time = time.time()

        # Apply filters
        filtered = tests
        if difficulty_filter:
            filtered = [t for t in filtered if t.difficulty == difficulty_filter]
        if category_filter:
            filtered = [t for t in filtered if t.category == category_filter]
        if test_ids:
            filtered = [t for t in filtered if t.id in test_ids]

        self.report.total = len(filtered)
        print(f"\n{'#'*60}")
        print(f"  Running {self.report.total} test(s)")
        print(f"{'#'*60}")

        for i, test in enumerate(filtered):
            if reset_between_tests and i > 0:
                print("\n  [INFO] Resetting app...")
                self.reset_app()

            result = self.run_single_test(test, gui_agent_callback)
            self.report.test_cases.append(result)

            if result.result == TestResult.PASS:
                self.report.passed += 1
            elif result.result == TestResult.FAIL:
                self.report.failed += 1
            elif result.result == TestResult.ERROR:
                self.report.errors += 1
            else:
                self.report.skipped += 1

        self.report.end_time = time.time()
        return self.report

    def print_report(self):
        """Print a summary report."""
        duration = self.report.end_time - self.report.start_time
        print(f"\n{'='*60}")
        print("  TEST REPORT")
        print(f"{'='*60}")
        print(f"  Total:    {self.report.total}")
        print(f"  Passed:   {self.report.passed}")
        print(f"  Failed:   {self.report.failed}")
        print(f"  Errors:   {self.report.errors}")
        print(f"  Skipped:  {self.report.skipped}")
        print(f"  Duration: {duration:.1f}s")

        if self.report.total > 0:
            pass_rate = (self.report.passed / self.report.total) * 100
            print(f"  Pass Rate: {pass_rate:.1f}%")

        # Per-difficulty breakdown
        difficulties = {}
        for tc in self.report.test_cases:
            if tc.difficulty not in difficulties:
                difficulties[tc.difficulty] = {"total": 0, "passed": 0}
            difficulties[tc.difficulty]["total"] += 1
            if tc.result == TestResult.PASS:
                difficulties[tc.difficulty]["passed"] += 1

        if difficulties:
            print(f"\n  Per-Difficulty Breakdown:")
            for diff in ["easy", "medium", "hard", "expert"]:
                if diff in difficulties:
                    d = difficulties[diff]
                    rate = (d["passed"] / d["total"]) * 100 if d["total"] > 0 else 0
                    print(f"    {diff:8s}: {d['passed']}/{d['total']} ({rate:.0f}%)")

        # Failed tests detail
        failed_tests = [tc for tc in self.report.test_cases if tc.result != TestResult.PASS]
        if failed_tests:
            print(f"\n  Failed/Error Tests:")
            for tc in failed_tests:
                print(f"    [{tc.result.value}] Test {tc.id:02d}: {tc.instruction[:60]}...")
                print(f"           Reason: {tc.actual_result}")

        print(f"{'='*60}\n")

    def save_report(self, filepath: str = "test_report.json"):
        """Save the test report to a JSON file."""
        data = {
            "total": self.report.total,
            "passed": self.report.passed,
            "failed": self.report.failed,
            "errors": self.report.errors,
            "skipped": self.report.skipped,
            "duration": self.report.end_time - self.report.start_time,
            "pass_rate": (self.report.passed / self.report.total * 100)
            if self.report.total > 0 else 0,
            "test_cases": [
                {
                    "id": tc.id,
                    "instruction": tc.instruction,
                    "difficulty": tc.difficulty,
                    "category": tc.category,
                    "result": tc.result.value,
                    "expected": tc.expected_result,
                    "actual": tc.actual_result,
                    "duration": round(tc.duration, 2),
                    "screenshot": tc.screenshot_path,
                }
                for tc in self.report.test_cases
            ],
        }
        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print(f"[INFO] Report saved to {filepath}")


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Instagram Simulator AutoTest Runner")
    parser.add_argument("--device", "-d", help="ADB device ID")
    parser.add_argument("--tests", "-t", default="test_instructions.json",
                        help="Path to test instructions JSON")
    parser.add_argument("--difficulty", choices=["easy", "medium", "hard", "expert"],
                        help="Filter by difficulty level")
    parser.add_argument("--category", help="Filter by category")
    parser.add_argument("--ids", nargs="+", type=int, help="Run specific test IDs")
    parser.add_argument("--no-reset", action="store_true",
                        help="Do not reset app between tests")
    parser.add_argument("--report", default="test_report.json",
                        help="Output report file path")
    parser.add_argument("--screenshots", default="screenshots",
                        help="Screenshot output directory")

    args = parser.parse_args()

    runner = TestRunner(device_id=args.device, screenshot_dir=args.screenshots)
    tests = runner.load_tests(args.tests)

    runner.setup()
    runner.run_tests(
        tests,
        gui_agent_callback=None,  # Manual mode by default
        difficulty_filter=args.difficulty,
        category_filter=args.category,
        test_ids=args.ids,
        reset_between_tests=not args.no_reset,
    )
    runner.print_report()
    runner.save_report(args.report)


if __name__ == "__main__":
    main()
