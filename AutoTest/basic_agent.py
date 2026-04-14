"""
Basic Agent Implementation
===========================
A simple agent wrapper when AppSim is not available.
This provides basic GUI automation using OpenAI-compatible APIs.
"""

import base64
import json
import logging
import os
import time
from datetime import datetime
from typing import Any, Dict, List, Optional

from openai import OpenAI
from pydantic import BaseModel

from common import get_adb, get_ui


class AgentExecutionResult(BaseModel):
    """Agent execution result data structure"""
    success: bool
    completed_steps: int
    total_actions: int
    executed_actions: List[Dict[str, Any]]
    screenshot_dir: str
    screenshots: List[str]
    error: Optional[str] = None
    final_message: Optional[str] = None

    class Config:
        arbitrary_types_allowed = True


class BasicAgent:
    """Basic GUI Agent using OpenAI-compatible API"""

    def __init__(
        self,
        api_key: str,
        base_url: str,
        model_name: str,
        device_id: str,
        screenshots_dir: str = "screenshots",
        max_steps: int = 15,
        model_kwargs: Optional[Dict] = None,
    ):
        self.api_key = api_key
        self.base_url = base_url
        self.model_name = model_name
        self.device_id = device_id
        self.screenshots_dir = screenshots_dir
        self.max_steps = max_steps
        self.model_kwargs = model_kwargs or {}

        # Initialize OpenAI client
        self.client = OpenAI(api_key=api_key, base_url=base_url)

        # Initialize ADB
        self.adb = get_adb(device_id)

        # History
        self.action_history = []
        self.screenshots = []

        # Create screenshots directory
        os.makedirs(screenshots_dir, exist_ok=True)

        logging.info(f"BasicAgent initialized with model: {model_name}")

    def reset(self):
        """Reset agent state"""
        self.action_history = []
        self.screenshots = []
        logging.info("Agent reset")

    def take_screenshot(self) -> str:
        """Take screenshot and return path"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S_%f")
        screenshot_path = os.path.join(self.screenshots_dir, f"screenshot_{timestamp}.png")
        self.adb.take_screenshot(screenshot_path)
        self.screenshots.append(screenshot_path)
        return screenshot_path

    def encode_image(self, image_path: str) -> str:
        """Encode image to base64"""
        with open(image_path, "rb") as f:
            return base64.b64encode(f.read()).decode("utf-8")

    def get_ui_description(self) -> str:
        """Get UI element descriptions"""
        ui = get_ui(self.adb)
        texts = ui.get_all_texts()
        descs = ui.get_all_descs()

        description = "UI Elements:\n"
        description += f"Texts: {texts[:20]}\n"  # Limit to first 20
        description += f"Descriptions: {descs[:20]}\n"

        return description

    def call_llm(self, instruction: str, screenshot_path: str, history: str) -> Dict:
        """Call LLM to get next action"""
        # Encode screenshot
        image_base64 = self.encode_image(screenshot_path)

        # Get UI description
        ui_desc = self.get_ui_description()

        # Build prompt
        system_prompt = """You are a GUI automation agent for an Instagram-like mobile app.
Your task is to execute user instructions by analyzing screenshots and UI elements, then deciding on actions.

Available actions:
1. tap(x, y) - Tap at coordinates
2. swipe(x1, y1, x2, y2) - Swipe gesture
3. input_text(text) - Input text
4. press_back() - Press back button
5. finish() - Task completed

Respond in JSON format:
{
    "reasoning": "Your reasoning about current state and what to do",
    "action": "action_name",
    "parameters": {...},
    "completed": false
}

When task is completed, set "completed": true and action: "finish"."""

        user_prompt = f"""Instruction: {instruction}

Action History:
{history if history else "No actions yet"}

Current UI:
{ui_desc}

Analyze the screenshot and decide the next action to execute the instruction."""

        # Call API
        try:
            response = self.client.chat.completions.create(
                model=self.model_name,
                messages=[
                    {"role": "system", "content": system_prompt},
                    {
                        "role": "user",
                        "content": [
                            {"type": "text", "text": user_prompt},
                            {
                                "type": "image_url",
                                "image_url": {"url": f"data:image/png;base64,{image_base64}"},
                            },
                        ],
                    },
                ],
                **self.model_kwargs,
            )

            # Parse response
            content = response.choices[0].message.content
            logging.debug(f"LLM Response: {content}")

            # Try to extract JSON
            if "```json" in content:
                json_str = content.split("```json")[1].split("```")[0].strip()
            elif "```" in content:
                json_str = content.split("```")[1].split("```")[0].strip()
            else:
                json_str = content.strip()

            result = json.loads(json_str)
            return result

        except Exception as e:
            logging.error(f"LLM call failed: {e}")
            return {
                "reasoning": f"Error: {e}",
                "action": "finish",
                "parameters": {},
                "completed": False,
            }

    def execute_action(self, action: str, parameters: Dict) -> bool:
        """Execute an action"""
        try:
            if action == "tap":
                x, y = parameters.get("x"), parameters.get("y")
                self.adb.tap(x, y)
                logging.info(f"Executed: tap({x}, {y})")
                return True

            elif action == "swipe":
                x1 = parameters.get("x1")
                y1 = parameters.get("y1")
                x2 = parameters.get("x2")
                y2 = parameters.get("y2")
                duration = parameters.get("duration", 300)
                self.adb.swipe(x1, y1, x2, y2, duration)
                logging.info(f"Executed: swipe({x1}, {y1}, {x2}, {y2})")
                return True

            elif action == "input_text":
                text = parameters.get("text", "")
                self.adb.input_text(text)
                logging.info(f"Executed: input_text('{text}')")
                return True

            elif action == "press_back":
                self.adb.press_back()
                logging.info("Executed: press_back()")
                return True

            elif action == "finish":
                logging.info("Task marked as finished")
                return True

            else:
                logging.warning(f"Unknown action: {action}")
                return False

        except Exception as e:
            logging.error(f"Action execution failed: {e}")
            return False

    def execute_instruction(self, instruction: str) -> AgentExecutionResult:
        """Execute instruction with agent"""
        logging.info(f"Executing instruction: {instruction}")

        self.reset()
        step = 0
        completed = False

        try:
            while step < self.max_steps and not completed:
                step += 1
                logging.info(f"\n--- Step {step}/{self.max_steps} ---")

                # Take screenshot
                screenshot_path = self.take_screenshot()

                # Build history string
                history_str = "\n".join(
                    [f"{i+1}. {a['action']} - {a['reasoning'][:100]}"
                     for i, a in enumerate(self.action_history)]
                )

                # Get next action from LLM
                decision = self.call_llm(instruction, screenshot_path, history_str)

                # Record action
                action_record = {
                    "step": step,
                    "reasoning": decision.get("reasoning", ""),
                    "action": decision.get("action", ""),
                    "parameters": decision.get("parameters", {}),
                    "screenshot": screenshot_path,
                }
                self.action_history.append(action_record)

                logging.info(f"Reasoning: {decision.get('reasoning', '')}")
                logging.info(f"Action: {decision.get('action', '')} {decision.get('parameters', {})}")

                # Check if completed
                if decision.get("completed", False) or decision.get("action") == "finish":
                    completed = True
                    logging.info("Task completed!")
                    break

                # Execute action
                success = self.execute_action(
                    decision.get("action", ""),
                    decision.get("parameters", {})
                )

                if not success:
                    logging.warning("Action execution failed, continuing...")

                # Wait for UI to update
                time.sleep(1.5)

            # Final screenshot
            final_screenshot = self.take_screenshot()

            return AgentExecutionResult(
                success=completed,
                completed_steps=step,
                total_actions=len(self.action_history),
                executed_actions=self.action_history,
                screenshot_dir=self.screenshots_dir,
                screenshots=self.screenshots,
                final_message=f"Completed in {step} steps" if completed else f"Reached max steps ({self.max_steps})",
            )

        except Exception as e:
            logging.error(f"Execution error: {e}")
            return AgentExecutionResult(
                success=False,
                completed_steps=step,
                total_actions=len(self.action_history),
                executed_actions=self.action_history,
                screenshot_dir=self.screenshots_dir,
                screenshots=self.screenshots,
                error=str(e),
            )

    def close(self):
        """Close agent"""
        logging.info("Agent closed")
