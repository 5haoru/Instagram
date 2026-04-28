"""
Agent Factory for Instagram Simulator
======================================
Creates agent instances with specified configurations.
All agents use BasicAgent (no appsim dependency).
"""

import os
from enum import Enum


class AgentEnum(Enum):
    """Agent type enumeration"""
    DOUBAO_SEED_18 = "Doubao-Seed-1.8"
    DOUBAO_SEED_20 = "Doubao-Seed-2.0-Pro"
    UI_TARS_15 = "UI-TARS-1.5"
    GPT4O = "GPT-4o"
    GPT5 = "GPT-5"
    GEMINI25_PRO = "Gemini-2.5-Pro"
    CLAUDE45_SONNET = "Claude-4.5-Sonnet"
    CLAUDE35_SONNET = "Claude-3.5-Sonnet"


# Agent config: env var prefix, default model name, extra model_kwargs
AGENT_CONFIGS = {
    "Doubao-Seed-1.8": ("DOUBAO_SEED_18", "ep-20260415155052-4htzp", {"temperature": 0.0, "top_p": 0.7}),
    "Doubao-Seed-2.0-Pro": ("DOUBAO_SEED_20", "ep-20260415155605-965gt", {"temperature": 0.0, "top_p": 0.7}),
    "UI-TARS-1.5": ("UI_TARS_15", "UI-TARS-1.5", {"temperature": 0.0, "top_p": 0.7}),
    "GPT-4o": ("GPT4O", "gpt-4o", {"temperature": 0.0}),
    "GPT-5": ("GPT5", "gpt-5", {"temperature": 0.0}),
    "Gemini-2.5-Pro": ("GEMINI25_PRO", "gemini-2.5-pro", {"temperature": 0.0}),
    "Claude-4.5-Sonnet": ("CLAUDE45_SONNET", "claude-4.5-sonnet", {"temperature": 0.0}),
    "Claude-3.5-Sonnet": ("CLAUDE35_SONNET", "claude-3-5-sonnet-20241022", {"temperature": 0.0}),
}


def create_agent(
    agent_name: str,
    device_id: str,
    screenshots_dir: str = "screenshots",
    max_steps: int = 15,
):
    if agent_name not in AGENT_CONFIGS:
        raise ValueError(f"Invalid agent name: {agent_name}. Choose from: {list(AGENT_CONFIGS.keys())}")

    prefix, default_model, model_kwargs = AGENT_CONFIGS[agent_name]

    api_key = os.getenv(f"{prefix}_API_KEY", os.getenv("API_KEY"))
    base_url = os.getenv(f"{prefix}_API_BASE", os.getenv("API_BASE"))
    model_name = os.getenv(f"{prefix}_MODEL_NAME", os.getenv("MODEL_NAME", default_model))

    from basic_agent import BasicAgent
    return BasicAgent(
        api_key=api_key,
        base_url=base_url,
        model_name=model_name,
        device_id=device_id,
        screenshots_dir=screenshots_dir,
        max_steps=max_steps,
        model_kwargs=model_kwargs,
    )
